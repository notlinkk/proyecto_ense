package com.mentory.ense_proyect.controller;

import com.mentory.ense_proyect.exception.DuplicatedUserException;
import com.mentory.ense_proyect.exception.UserNotFoundException;
import com.mentory.ense_proyect.model.dto.SubscriptionResponseDTO;
import com.mentory.ense_proyect.model.entity.Lesson;
import com.mentory.ense_proyect.model.entity.Subscription;
import com.mentory.ense_proyect.model.entity.User;
import com.mentory.ense_proyect.service.SubscriptionService;
import com.mentory.ense_proyect.service.UserService;

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


import com.fasterxml.jackson.annotation.JsonView;

import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.JsonPatchOperation;

import java.util.*;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@RestController
@RequestMapping("users")
@ExposesResourceFor(User.class) 
public class UserController {
    UserService userService;
    SubscriptionService subscriptionService;
    private EntityLinks entityLinks;

    @Autowired
    public UserController(UserService userService, SubscriptionService subscriptionService, EntityLinks entityLinks) {
        this.userService = userService;
        this.subscriptionService = subscriptionService;
        this.entityLinks = entityLinks;
    }

    /**
     * Obtiene el usuario actual autenticado.
     * Este endpoint es usado por el frontend para obtener los datos del usuario logueado.
     */
    @GetMapping(path = "me")
    public ResponseEntity<User> getCurrentUser() throws UserNotFoundException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        return ResponseEntity.ok(userService.getUser(username));
    }

    /**
     * Obtiene las lecciones creadas por el usuario actual.
     * El count se puede obtener del campo totalElements de la respuesta paginada.
     */
    @GetMapping(path = "me/lessons")
    public ResponseEntity<Page<com.mentory.ense_proyect.model.entity.Lesson>> getMyLessons(
            @RequestParam(value="page", required=false, defaultValue="0") int page,
            @RequestParam(value="size", required=false, defaultValue="10") int size
    ) throws UserNotFoundException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Page<com.mentory.ense_proyect.model.entity.Lesson> lessons = userService.getUserLessons(username, PageRequest.of(page, size));
        return ResponseEntity.ok(lessons);
    }

    /**
     * Obtiene las suscripciones del usuario actual.
     * Devuelve un DTO con la información de la lección incluida.
     */
    @GetMapping(path = "me/subscriptions")
    public ResponseEntity<Page<SubscriptionResponseDTO>> getMySubscriptions(
            @RequestParam(value="page", required=false, defaultValue="0") int page,
            @RequestParam(value="size", required=false, defaultValue="10") int size
    ) throws UserNotFoundException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Page<Subscription> subscriptions = subscriptionService.getUserSubscriptions(
            username, PageRequest.of(page, size)
        );
        
        // Convert to DTOs with lesson info
        Page<SubscriptionResponseDTO> dtoPage = subscriptions.map(sub -> {
            Lesson lesson = sub.getLesson();
            SubscriptionResponseDTO.LessonSummaryDTO lessonDto = lesson != null 
                ? new SubscriptionResponseDTO.LessonSummaryDTO(
                    lesson.getId(),
                    lesson.getName(),
                    lesson.getDescription()
                )
                : null;
            return new SubscriptionResponseDTO(
                sub.getId(),
                sub.getStartDate(),
                sub.getEndDate(),
                sub.getPrize(),
                sub.isActive(),
                lessonDto
            );
        });
        
        return ResponseEntity.ok(dtoPage);
    }

    @GetMapping(path = "{id}", version = "0")
    public ResponseEntity <User> getUserV0(@PathVariable("id") String id) throws UserNotFoundException {
        return ResponseEntity.ok(userService.getUser(id));
    }

    @GetMapping(path = "{id}", version = "1")
    public ResponseEntity <EntityModel<User>> getUserV1(@PathVariable("id") String id) throws UserNotFoundException {

        EntityModel<User> user = EntityModel.of(userService.getUser(id));
        user.add(
            entityLinks.linkToItemResource(User.class, user).withSelfRel(),
            entityLinks.linkToCollectionResource(User.class).withRel(IanaLinkRelations.COLLECTION),
            entityLinks.linkToItemResource(User.class, user).withRel("delete").withType("DELETE")
        );
        return ResponseEntity.ok(user);
    }

    @GetMapping( version = "0")
    public ResponseEntity<Page<User>> getUsersV0(
            @RequestParam(value="nombre", required=false) String nombre,
            @RequestParam(value="page", required=false, defaultValue="0") int page,
            @RequestParam(value="size", required=false, defaultValue="2") int pagesize,
            @RequestParam(value="sort", required=false, defaultValue="") List<String> sort
    ) {
        Page<User> users = userService.getUsers(
                nombre,
                PageRequest.of(
                        page, pagesize,
                        Sort.by(sort.stream()
                                .map(key -> key.startsWith("-") ?
                                        Sort.Order.desc(key.substring(1)) :
                                        Sort.Order.asc(key))
                                .toList()
                        )
                )
        );

        if (users.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(users);
    }
    
    @GetMapping(version = "1")
    public ResponseEntity<PagedModel<User>> getUsersV1(
        @RequestParam(value="nombre", required=false) String nombre,
        @RequestParam(value="page", required=false, defaultValue="0") int page,
        @RequestParam(value="size", required=false, defaultValue="2") int pagesize,
        @RequestParam(value="sort", required=false, defaultValue="") List<String> sort
    ) {

        var users = userService.getUsers(
        nombre,
        PageRequest.of(
                page, pagesize,
                Sort.by(sort.stream()
                        .map(key -> key.startsWith("-") ?
                                Sort.Order.desc(key.substring(1)) :
                                Sort.Order.asc(key))
                        .toList()
                )
        )
        );

        if (users.isEmpty()) {
        return ResponseEntity.noContent().build();
        }

        PagedModel<User> response = PagedModel.of(
        users.getContent(),
        new PagedModel.PageMetadata(
                users.getSize(),
                users.getNumber(),
                users.getTotalElements(),
                users.getTotalPages()
        )
        );

        response.add(
                entityLinks.linkToCollectionResource(User.class).withSelfRel()

        );

        if (users.hasNext()) {
        response.add(
                linkTo(methodOn(UserController.class)
                        .getUsersV1(nombre, page + 1, pagesize, sort))
                        .withRel(IanaLinkRelations.NEXT)
        );
        }

        if (users.hasPrevious()) {
        response.add(
                linkTo(methodOn(UserController.class)
                        .getUsersV1(nombre, page - 1, pagesize, sort))
                        .withRel(IanaLinkRelations.PREVIOUS)
        );
        }

        return ResponseEntity.ok(response);
    }


    @PostMapping
    @JsonView(User.CreateView.class)
    public ResponseEntity<User> createUser(@RequestBody User users) throws DuplicatedUserException {
        User newUser = userService.create(users);
        return ResponseEntity
                .created(MvcUriComponentsBuilder
                        .fromMethodName(UserController.class, "getUser", users.getUsername())
                        .build()
                        .toUri())
                .body(newUser);
    }

    @DeleteMapping({"{id}"})
    public ResponseEntity<Void> deleteUser(@PathVariable("id") String id) throws UserNotFoundException {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("{id}")
    public ResponseEntity<User> updateUser(
            @PathVariable("id") String id,
            @RequestBody List<JsonPatchOperation> changes
    ) throws UserNotFoundException, JsonPatchException {
        return ResponseEntity.ok(userService.updateUser(id, changes));
    }
}
