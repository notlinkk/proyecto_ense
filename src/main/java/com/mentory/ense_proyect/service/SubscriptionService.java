package com.mentory.ense_proyect.service;

import com.mentory.ense_proyect.exception.*;
import com.mentory.ense_proyect.model.Subscription;
import com.mentory.ense_proyect.repository.SubscriptionRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;

import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonpatch.JsonPatchOperation;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.*;

@Service
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final ObjectMapper mapper;

    @Autowired
    public SubscriptionService(SubscriptionRepository subscriptionRepository, ObjectMapper mapper) {
        this.subscriptionRepository = subscriptionRepository;
        this.mapper=mapper;
    }

    // CRUD
    public Subscription addSubscription(Subscription subscription) throws DuplicatedSubscriptionException {
        if (!subscriptionRepository.exists(Example.of(subscription))) {
            return subscriptionRepository.save(subscription);
        } else {
            throw new DuplicatedSubscriptionException(subscription);
        }
    }

    public Page<@NonNull Subscription> getSubscriptions(@Nullable String id, PageRequest page) {
        Example<Subscription> example = Example.of(new Subscription(id, null, 0, false, null, null));
        return subscriptionRepository.findAll(example, page);
    }

    public Subscription getSubscription(String id) throws SubscriptionNotFoundException {
        return subscriptionRepository.findById(id).orElseThrow(() -> new SubscriptionNotFoundException(id));
    }

    public Subscription updateSubscription(String id, List<JsonPatchOperation> changes) throws SubscriptionNotFoundException, JsonPatchException {
        Subscription subscription = subscriptionRepository.findById(id).orElseThrow(() -> new SubscriptionNotFoundException(id));
            JsonPatch patch = new JsonPatch(changes);
            JsonNode patched = patch.apply(mapper.convertValue(subscription, JsonNode.class));
            Subscription subscriptionPatched = mapper.convertValue(patched, Subscription.class);
            return subscriptionRepository.save(subscriptionPatched);
    }

    public void deleteSubscription(String id) throws SubscriptionNotFoundException {
        if (subscriptionRepository.existsById(id)) {
            subscriptionRepository.deleteById(id);
        } else {
            throw new SubscriptionNotFoundException(id);
        }
    }
}
