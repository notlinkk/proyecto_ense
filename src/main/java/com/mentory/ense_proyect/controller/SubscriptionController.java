package com.mentory.ense_proyect.controller;

import com.mentory.ense_proyect.exception.DuplicatedSubscriptionException;
import com.mentory.ense_proyect.exception.LessonNotFoundException;
import com.mentory.ense_proyect.exception.SubscriptionNotFoundException;
import com.mentory.ense_proyect.exception.UserNotFoundException;
import com.mentory.ense_proyect.model.dto.SubscriptionDTO;
import com.mentory.ense_proyect.model.dto.SubscriptionResponseDTO;
import com.mentory.ense_proyect.model.entity.Lesson;
import com.mentory.ense_proyect.model.entity.Subscription;
import com.mentory.ense_proyect.service.SubscriptionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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

import org.springframework.security.access.prepost.PreAuthorize;

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

    @GetMapping(path="{id}", headers = "API-Version=0")
    @PreAuthorize("hasAuthority('subscriptions:read')")
    public ResponseEntity<Subscription> getSubscriptionV0(@PathVariable("id") String id, Authentication authentication) throws SubscriptionNotFoundException{
        String username = authentication.getName();
        return ResponseEntity.ok(subscriptionService.getSubscriptionWithAccessControl(id, username));
    }
    @GetMapping(path="{id}", headers = "API-Version=1")
    @PreAuthorize("hasAuthority('subscriptions:read')")
    public ResponseEntity<EntityModel<Subscription>> getSubscriptionV1(@PathVariable("id") String id, Authentication authentication) throws SubscriptionNotFoundException{
        String username = authentication.getName();
        EntityModel<Subscription> subscription = EntityModel.of(subscriptionService.getSubscriptionWithAccessControl(id, username));
        subscription.add(
            entityLinks.linkToItemResource(Subscription.class, subscription.getContent().getId()).withSelfRel(),
            entityLinks.linkToCollectionResource(Subscription.class).withRel(IanaLinkRelations.COLLECTION),
            entityLinks.linkToItemResource(Subscription.class, subscription.getContent().getId()).withRel("delete").withType("DELETE")
        );
        return ResponseEntity.ok(subscription);
    }

    @GetMapping(headers = "API-Version=0")
    @PreAuthorize("hasAuthority('subscriptions:read')")
    public ResponseEntity<Page<Subscription>> getSubscriptionsV0(
            @RequestParam(value="page", required=false, defaultValue="0") int page,
            @RequestParam(value="size", required=false, defaultValue="10") int pagesize,
            @RequestParam(value="sort", required=false, defaultValue="") List<String> sort,
            Authentication authentication
    )
    {
        String username = authentication.getName();
        Page<Subscription> subscriptions = subscriptionService.getSubscriptionsWithAccessControl(
                username,
                PageRequest.of(page, pagesize)
        );

        if (subscriptions.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(subscriptions);
    }

    @GetMapping(headers = "API-Version=1")
    @PreAuthorize("hasAuthority('subscriptions:read')")
    public ResponseEntity<PagedModel<Subscription>> getSubscriptionsV1(
            @RequestParam(value="page", required=false, defaultValue="0") int page,
            @RequestParam(value="size", required=false, defaultValue="10") int pagesize,
            @RequestParam(value="sort", required=false, defaultValue="") List<String> sort,
            Authentication authentication
    )
    {
        String username = authentication.getName();
        Page<Subscription> subscriptions = subscriptionService.getSubscriptionsWithAccessControl(
                username,
                PageRequest.of(page, pagesize)
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
                    .getSubscriptionsV1(page +1, pagesize, sort, authentication))
                    .withRel(IanaLinkRelations.NEXT)
            );
        }

        if(subscriptions.hasPrevious()){
            response.add(
                linkTo(methodOn(SubscriptionController.class)
                    .getSubscriptionsV1(page -1, pagesize, sort, authentication))
                    .withRel(IanaLinkRelations.PREVIOUS)
            );
        }

        return ResponseEntity.ok(response);
    }

    /**
     * Subscribe to a lesson. Uses authenticated user and lesson ID from DTO.
     */
    @PostMapping
    @PreAuthorize("hasAuthority('subscriptions:write')")
    public ResponseEntity<Subscription> createSubscription(
            @RequestBody SubscriptionDTO subscriptionDTO,
            Authentication authentication
    ) throws DuplicatedSubscriptionException, LessonNotFoundException, UserNotFoundException {
        String username = authentication.getName();
        Subscription newSubscription = subscriptionService.createSubscription(subscriptionDTO, username);
        return ResponseEntity
                .created(MvcUriComponentsBuilder
                        .fromMethodName(SubscriptionController.class, "getSubscriptionV1", newSubscription.getId())
                        .build()
                        .toUri())
                .body(newSubscription);
    }

    /**
     * Check if user has active subscription to a lesson.
     */
    @GetMapping("check/{lessonId}")
    @PreAuthorize("hasAuthority('subscriptions:read')")
    public ResponseEntity<Map<String, Boolean>> checkSubscription(
            @PathVariable("lessonId") String lessonId,
            Authentication authentication
    ) throws LessonNotFoundException {
        String username = authentication.getName();
        boolean hasAccess = subscriptionService.canAccessLessonContent(username, lessonId);
        return ResponseEntity.ok(Map.of("hasAccess", hasAccess));
    }

    @PatchMapping("{id}")
    @PreAuthorize("hasAuthority('subscriptions:update')")
    public ResponseEntity<Subscription> updateSubscription(
            @PathVariable("id") String id,
            @RequestBody List<JsonPatchOperation> changes
    ) throws SubscriptionNotFoundException, JsonPatchException {
        return ResponseEntity.ok(subscriptionService.updateSubscription(id, changes));
    }

    @DeleteMapping({"{id}"})
    @PreAuthorize("hasAuthority('subscriptions:delete')")
    public ResponseEntity<Void> deleteSubscription(@PathVariable("id") String id) throws
            SubscriptionNotFoundException {
        subscriptionService.deleteSubscription(id);
        return ResponseEntity.noContent().build();
    }





}