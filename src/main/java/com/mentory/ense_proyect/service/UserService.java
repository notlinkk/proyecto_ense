package com.mentory.ense_proyect.service;

import com.mentory.ense_proyect.exception.UserNotFoundException;
import com.mentory.ense_proyect.exception.DuplicatedUserException;
import com.mentory.ense_proyect.model.User;
import com.mentory.ense_proyect.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.JsonPatchOperation;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.*;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final ObjectMapper mapper;

    @Autowired  
    public UserService (UserRepository userRepository, ObjectMapper mapper) {
        this.userRepository = userRepository;
        this.mapper = new ObjectMapper();

        userRepository.save(
                new User("fernandaco2011", "Fernando", "García", "Rodriguez", "fernandito54@gmail.com", "1234")
        );
    }

    // CRUD
    public User addUser(User user) throws DuplicatedUserException {
        if (!userRepository.exists(Example.of(user))) {
            return userRepository.save(user);
        } else {
            throw new DuplicatedUserException(user);
        }
    }

    public User updateUser(String username, List<JsonPatchOperation> changes) throws UserNotFoundException, JsonPatchException {
        User user = userRepository.findById(username).orElseThrow(() -> new UserNotFoundException(username));
        JsonPatch patch = new JsonPatch(changes);
        JsonNode patched = patch.apply(mapper.convertValue(user, JsonNode.class));
        User updated = mapper.convertValue(patched, User.class);
        return userRepository.save(updated);
    }

    public Page<@NonNull User> getUsers(@Nullable String name, PageRequest page) {
        Example<User> example = Example.of(new User(name, null, null, null, null, null));
        return userRepository.findAll(example, page);
    }

    public User getUser(String id) throws UserNotFoundException {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
    }



    public void deleteUser(String id) throws UserNotFoundException {
        if(userRepository.existsById(id)){
            userRepository.deleteById(id);
        } else {
            throw new UserNotFoundException(id);
        }
    }


}
