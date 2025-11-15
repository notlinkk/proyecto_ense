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

import com.fasterxml.jackson.annotation.JsonView;

import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.JsonPatchOperation;

import java.util.*;

@RestController
@RequestMapping("users")
public class UserController {
    UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("{id}")
    public ResponseEntity <User> getUser(@PathVariable("id") String id) throws UserNotFoundException {
        return ResponseEntity.ok(userService.getUser(id));
    }

    @GetMapping
    public ResponseEntity<Page<User>> getUsers(
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
