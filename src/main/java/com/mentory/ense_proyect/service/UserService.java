package com.mentory.ense_proyect.service;

import com.mentory.ense_proyect.exception.UserNotFoundException;
import com.mentory.ense_proyect.model.entity.Role;
import com.mentory.ense_proyect.model.entity.User;
import com.mentory.ense_proyect.exception.DuplicatedUserException;
import com.mentory.ense_proyect.repository.LessonRepository;
import com.mentory.ense_proyect.repository.RoleRepository;
import com.mentory.ense_proyect.repository.UserRepository;

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
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final LessonRepository lessonRepository;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper mapper;


    @Autowired  
    public UserService (UserRepository userRepository, RoleRepository roleRepository, 
                        LessonRepository lessonRepository, PasswordEncoder passwordEncoder, ObjectMapper mapper) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.lessonRepository = lessonRepository;
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
    user.getRoles().add(userRole);

    user.setPassword(passwordEncoder.encode(user.getPassword()));

    return userRepository.save(user);
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

    /**
     * Obtiene las lecciones creadas por un usuario con paginación.
     * Busca por ownerId en lugar de usar la relación many-to-many.
     */
    public Page<com.mentory.ense_proyect.model.entity.Lesson> getUserLessons(String username, PageRequest page) throws UserNotFoundException {
        if (!userRepository.existsById(username)) {
            throw new UserNotFoundException(username);
        }
        return lessonRepository.findByOwnerId(username, page);
    }

    public void deleteUser(String id) throws UserNotFoundException {
        if(userRepository.existsById(id)){
            userRepository.deleteById(id);
        } else {
            throw new UserNotFoundException(id);
        }
    }

    /**
     * Adds a role to the current user.
     * Only allows adding the TEACHER role.
     * @param username The username of the user
     * @param rolename The role to add (must be "TEACHER")
     * @return The updated user
     */
    public User addRoleToCurrentUser(String username, String rolename) throws UserNotFoundException {
        if (!"TEACHER".equals(rolename)) {
            throw new IllegalArgumentException("Only adding TEACHER role is allowed");
        }
        
        User user = userRepository.findById(username)
            .orElseThrow(() -> new UserNotFoundException(username));
        
        // Check if user already has the role
        Role teacherRole = roleRepository.findByRolename("TEACHER");
        if (teacherRole != null && !user.getRoles().contains(teacherRole)) {
            user.getRoles().add(teacherRole);
        }
        
        return userRepository.save(user);
    }

    /**
     * Checks if a user has the TEACHER role.
     * @param username The username to check
     * @return true if the user is a teacher
     */
    public boolean isTeacher(String username) throws UserNotFoundException {
        User user = userRepository.findById(username)
            .orElseThrow(() -> new UserNotFoundException(username));
        
        return user.getRoles().stream()
            .anyMatch(role -> "TEACHER".equals(role.getRolename()) || "ADMIN".equals(role.getRolename()));
    }


}
