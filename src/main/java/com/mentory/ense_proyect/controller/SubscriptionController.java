package com.mentory.ense_proyect.controller;

import com.mentory.ense_proyect.exception.DuplicatedSubscriptionException;
import com.mentory.ense_proyect.exception.SubscriptionNotFoundException;
import com.mentory.ense_proyect.model.Subscription;
import com.mentory.ense_proyect.service.SubscriptionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import com.github.fge.jsonpatch.JsonPatchOperation;
import com.github.fge.jsonpatch.JsonPatchException;

import java.util.*;

@RestController
@RequestMapping("subscriptions")
public class SubscriptionController {
    SubscriptionService subscriptionService;

    @Autowired
    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @GetMapping("{id}")
    public ResponseEntity<Subscription> getSubscription(@PathVariable("id") String id) throws SubscriptionNotFoundException{
        return ResponseEntity.ok(subscriptionService.getSubscription(id));
    }

    @GetMapping
    public ResponseEntity<Page<Subscription>> getSubscriptions(
            @RequestParam(value="id", required=false) String id,
            @RequestParam(value="page", required=false, defaultValue="0") int page,
            @RequestParam(value="size", required=false, defaultValue="2") int pagesize,
            @RequestParam(value="sort", required=false, defaultValue="") List<String> sort
    )
    {
        Page<Subscription> subscriptions = subscriptionService.getSubscriptions(
                null,
                PageRequest.of(
                        0, 10,
                        Sort.by(List.of(Sort.Order.asc("id")))
                )
        );

        if (subscriptions.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(subscriptions);
    }

    @PostMapping
    public ResponseEntity<Subscription> createSusbcription(@RequestBody Subscription subscription) throws DuplicatedSubscriptionException {
            Subscription newSubscription = subscriptionService.addSubscription(subscription);
            return ResponseEntity
                    .created(MvcUriComponentsBuilder
                            .fromMethodName(SubscriptionController.class, "getSubscription", subscription.getId())
                            .build()
                            .toUri())
                    .body(newSubscription);
    }

    @PatchMapping("{id}")
    public ResponseEntity<Subscription> updateSubscription(
            @PathVariable("id") String id,
            @RequestBody List<JsonPatchOperation> changes
    ) throws SubscriptionNotFoundException, JsonPatchException {
        return ResponseEntity.ok(subscriptionService.updateSubscription(id, changes));
    }

    @DeleteMapping({"{id}"})
    public ResponseEntity<Void> deleteSubscription(@PathVariable("id") String id) throws
            SubscriptionNotFoundException {
        subscriptionService.deleteSubscription(id);
        return ResponseEntity.noContent().build();
    }





}