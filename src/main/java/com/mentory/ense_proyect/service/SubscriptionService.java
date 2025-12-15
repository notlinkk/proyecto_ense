package com.mentory.ense_proyect.service;

import com.mentory.ense_proyect.exception.*;
import com.mentory.ense_proyect.model.dto.SubscriptionDTO;
import com.mentory.ense_proyect.model.entity.Lesson;
import com.mentory.ense_proyect.model.entity.Subscription;
import com.mentory.ense_proyect.model.entity.User;
import com.mentory.ense_proyect.repository.LessonRepository;
import com.mentory.ense_proyect.repository.SubscriptionRepository;
import com.mentory.ense_proyect.repository.UserRepository;

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

import java.time.LocalDate;
import java.util.*;

@Service
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final LessonRepository lessonRepository;
    private final UserRepository userRepository;
    private final ObjectMapper mapper;

    @Autowired
    public SubscriptionService(SubscriptionRepository subscriptionRepository, 
                               LessonRepository lessonRepository,
                               UserRepository userRepository,
                               ObjectMapper mapper) {
        this.subscriptionRepository = subscriptionRepository;
        this.lessonRepository = lessonRepository;
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    // CRUD
    public Subscription addSubscription(Subscription subscription) throws DuplicatedSubscriptionException {
        if (!subscriptionRepository.exists(Example.of(subscription))) {
            return subscriptionRepository.save(subscription);
        } else {
            throw new DuplicatedSubscriptionException(subscription);
        }
    }

    /**
     * Create a subscription from a DTO for the authenticated user.
     */
    public Subscription createSubscription(SubscriptionDTO dto, String username) 
            throws LessonNotFoundException, UserNotFoundException, DuplicatedSubscriptionException {
        
        // Check if user already has subscription to this lesson
        if (subscriptionRepository.existsByBuyerUsernameAndLessonId(username, dto.lessonId())) {
            throw new DuplicatedSubscriptionException("You already have a subscription to this lesson");
        }
        
        Lesson lesson = lessonRepository.findById(dto.lessonId())
                .orElseThrow(() -> new LessonNotFoundException(dto.lessonId()));
        
        User user = userRepository.findById(username)
                .orElseThrow(() -> new UserNotFoundException(username));
        
        // Owner cannot subscribe to their own lesson
        if (lesson.getOwnerId().equals(username)) {
            throw new IllegalArgumentException("Cannot subscribe to your own lesson");
        }
        
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusMonths(1);
        
        Subscription subscription = new Subscription(
            startDate.toString(),         // startDate
            endDate.toString(),           // endDate (1 month later)
            true,                         // active
            user,
            lesson
        );
        subscription.setId(UUID.randomUUID().toString());
        
        return subscriptionRepository.save(subscription);
    }

    /**
     * Check if user has active subscription to a lesson.
     */
    public boolean hasActiveSubscription(String username, String lessonId) {
        return subscriptionRepository.findByBuyerUsernameAndLessonIdAndActiveTrue(username, lessonId).isPresent();
    }

    /**
     * Check if user can access lesson content (is owner or has subscription).
     */
    public boolean canAccessLessonContent(String username, String lessonId) throws LessonNotFoundException {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new LessonNotFoundException(lessonId));
        
        // Owner can always access
        if (lesson.getOwnerId().equals(username)) {
            return true;
        }
        
        // Check for active subscription
        return hasActiveSubscription(username, lessonId);
    }

    /**
     * Get subscriptions for a user.
     */
    public Page<Subscription> getUserSubscriptions(String username, PageRequest page) {
        return subscriptionRepository.findByBuyerUsername(username, page);
    }

    public Page<@NonNull Subscription> getSubscriptions(@Nullable String id, PageRequest page) {
        Subscription example = new Subscription();
        example.setId(id);
        return subscriptionRepository.findAll(Example.of(example), page);
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
