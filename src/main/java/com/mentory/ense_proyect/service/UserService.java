package com.mentory.ense_proyect.service;

import com.mentory.ense_proyect.exception.UserNotFoundException;
import com.mentory.ense_proyect.exception.DuplicatedUserException;
import com.mentory.ense_proyect.model.Role;
import com.mentory.ense_proyect.model.User;
import com.mentory.ense_proyect.repository.RoleRepository;
import com.mentory.ense_proyect.repository.UserRepository;

import io.swagger.v3.core.util.Json;
import tools.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.data.domain.Page;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.JsonPatchOperation;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.*;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper mapper;


    @Autowired  
    public UserService (UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, ObjectMapper mapper) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.mapper = mapper;
    }

    @ Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
    }

    public User create(User user) throws DuplicatedUserException {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new DuplicatedUserException(user);
        }

        Role userRole = roleRepository.findByRolename("USER");

        return userRepository.save(
                new User(
                        user.getUsername(),
                        passwordEncoder.encode(user.getPassword()),
                        Set.of(userRole)
                )
        );
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
