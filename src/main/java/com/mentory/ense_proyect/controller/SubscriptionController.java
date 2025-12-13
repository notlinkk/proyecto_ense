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

import org.springframework.hateoas.*;
import org.springframework.hateoas.server.EntityLinks;
import org.springframework.hateoas.server.ExposesResourceFor;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

import com.github.fge.jsonpatch.JsonPatchOperation;
import com.github.fge.jsonpatch.JsonPatchException;

import java.util.*;

@RestController
@RequestMapping("subscriptions")
@ExposesResourceFor(Subscription.class)
public class SubscriptionController {
    SubscriptionService subscriptionService;
    private EntityLinks entityLinks;

    @Autowired
    public SubscriptionController(SubscriptionService subscriptionService, EntityLinks entityLinks) {
        this.subscriptionService = subscriptionService;
        this.entityLinks = entityLinks;
    }

    @GetMapping(path="{id}", version = "0")
    public ResponseEntity<Subscription> getSubscriptionV0(@PathVariable("id") String id) throws SubscriptionNotFoundException{
        return ResponseEntity.ok(subscriptionService.getSubscription(id));
    }
    @GetMapping(path="{id}", version = "1")
    public ResponseEntity<EntityModel<Subscription>> getSubscriptionV1(@PathVariable("id") String id) throws SubscriptionNotFoundException{
        EntityModel<Subscription> subscription = EntityModel.of(subscriptionService.getSubscription(id));
        subscription.add(
            entityLinks.linkToItemResource(Subscription.class, subscription).withSelfRel(),
            entityLinks.linkToCollectionResource(Subscription.class).withRel(IanaLinkRelations.COLLECTION),
            entityLinks.linkToItemResource(Subscription.class, subscription).withRel("delete").withType("DELETE")
        );
        return ResponseEntity.ok(subscription);
    }

    @GetMapping(version = "0")
    public ResponseEntity<Page<Subscription>> getSubscriptionsV0(
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

    @GetMapping(version = "1")
    public ResponseEntity<PagedModel<Subscription>> getSubscriptionsV1(
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

        PagedModel<Subscription> response= PagedModel.of(
            subscriptions.getContent(),
            new PagedModel.PageMetadata(
                subscriptions.getSize(),
                subscriptions.getNumber(),
                subscriptions.getTotalElements(),
                subscriptions.getTotalPages()
            )
        );

        response.add( entityLinks.linkToCollectionResource(Subscription.class).withSelfRel() );

        if(subscriptions.hasNext()){
            response.add(
                linkTo(methodOn(SubscriptionController.class)
                    .getSubscriptionsV1(id, page +1, pagesize, sort))
                    .withRel(IanaLinkRelations.NEXT)
            );
        }

        if(subscriptions.hasPrevious()){
            response.add(
                linkTo(methodOn(SubscriptionController.class)
                    .getSubscriptionsV1(id, page -1, pagesize, sort))
                    .withRel(IanaLinkRelations.PREVIOUS)
            );
        }

        return ResponseEntity.ok(response);
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