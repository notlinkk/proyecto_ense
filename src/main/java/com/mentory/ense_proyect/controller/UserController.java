package com.mentory.ense_proyect.controller;

import com.mentory.ense_proyect.exception.DuplicatedUserException;
import com.mentory.ense_proyect.exception.UserNotFoundException;
import com.mentory.ense_proyect.model.User;
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

@RestController
@RequestMapping("users")
@ExposesResourceFor(User.class) 
public class UserController {
    UserService userService;
    private EntityLinks entityLinks;

    @Autowired
    public UserController(UserService userService, EntityLinks entityLinks) {
        this.userService = userService;
        this.entityLinks = entityLinks;
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
        User newUser = userService.addUser(users);
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
