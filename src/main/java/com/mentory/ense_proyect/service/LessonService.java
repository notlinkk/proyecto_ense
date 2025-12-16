package com.mentory.ense_proyect.service;

import com.mentory.ense_proyect.exception.*;
import com.mentory.ense_proyect.model.dto.LessonDTO;
import com.mentory.ense_proyect.model.entity.Ability;
import com.mentory.ense_proyect.model.entity.Lesson;
import com.mentory.ense_proyect.model.entity.User;
import com.mentory.ense_proyect.repository.AbilityRepository;
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

import java.util.*;

@Service
public class LessonService {
    private final LessonRepository lessonRepository;
    private final AbilityRepository abilityRepository;
    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final ObjectMapper mapper;

    @Autowired
    public LessonService(LessonRepository leccionRepository, AbilityRepository abilityRepository, 
                         UserRepository userRepository, SubscriptionRepository subscriptionRepository,
                         ObjectMapper mapper) {
        this.lessonRepository = leccionRepository;
        this.abilityRepository = abilityRepository;
        this.userRepository = userRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.mapper = mapper;
    }

    /**
     * Check if user can access lesson content (modules).
     * User can access if: is owner, has active subscription, or is ADMIN.
     */
    public boolean canAccessLessonContent(String username, Lesson lesson) {
        // Owner can always access
        if (lesson.getOwnerId().equals(username)) {
            return true;
        }
        // Check for active subscription
        return subscriptionRepository.findByBuyerUsernameAndLessonIdAndActiveTrue(username, lesson.getId()).isPresent();
    }

    // CRUD
    public Lesson addLesson(Lesson lesson) throws DuplicatedLessonException {
        if (!lessonRepository.exists(Example.of(lesson))) {
            return lessonRepository.save(lesson);
        } else {
            throw new DuplicatedLessonException(lesson);
        }
    }

    /**
     * Crea una lección a partir de un DTO y el ID del propietario.
     * Genera un ID único para la lección.
     * Valida que se incluya al menos una habilidad.
     */
    public Lesson createLesson(LessonDTO dto, String ownerId) throws DuplicatedLessonException, IllegalArgumentException {
        // Validar que se incluya al menos una habilidad
        if (dto.abilities() == null || dto.abilities().isEmpty()) {
            throw new IllegalArgumentException("A lesson must have at least one ability");
        }
        
        // Buscar las habilidades existentes
        Set<Ability> abilities = new HashSet<>();
        for (String abilityName : dto.abilities()) {
            Ability ability = abilityRepository.findById(abilityName)
                .orElseThrow(() -> new IllegalArgumentException("Ability not found: " + abilityName));
            abilities.add(ability);
        }
        
        Lesson lesson = new Lesson(ownerId, dto.name(), dto.description());
        lesson.setId(UUID.randomUUID().toString());
        lesson.setHabilidad(abilities);
        
        // Establecer precio e imagen si se proporcionan
        if (dto.price() != null) {
            lesson.setPrice(dto.price());
        }
        if (dto.imageUrl() != null && !dto.imageUrl().isBlank()) {
            lesson.setImageUrl(dto.imageUrl());
        }
        
        return lessonRepository.save(lesson);
    }

    public Page<@NonNull Lesson> getLessons(@Nullable String name, @Nullable String currentUserId, PageRequest page) {
        Page<Lesson> lessons;
        
        if (currentUserId == null || currentUserId.isBlank()) {
            // No user context - return all lessons
            if (name == null || name.isBlank()) {
                lessons = lessonRepository.findAll(page);
            } else {
                lessons = lessonRepository.findByNameContainingIgnoreCase(name, page);
            }
        } else {
            // Exclude lessons owned by the current user
            if (name == null || name.isBlank()) {
                lessons = lessonRepository.findByOwnerIdNot(currentUserId, page);
            } else {
                lessons = lessonRepository.findByNameContainingIgnoreCaseAndOwnerIdNot(name, currentUserId, page);
            }
        }
        
        // Populate owner names for each lesson
        lessons.forEach(this::populateOwnerName);
        
        return lessons;
    }

    /**
     * Populates the ownerName field of a lesson with the owner's full name.
     */
    private void populateOwnerName(Lesson lesson) {
        if (lesson.getOwnerId() != null) {
            userRepository.findById(lesson.getOwnerId()).ifPresent(owner -> {
                String fullName = owner.getName();
                if (owner.getSurname1() != null && !owner.getSurname1().isBlank()) {
                    fullName += " " + owner.getSurname1();
                }
                lesson.setOwnerName(fullName);
            });
        }
    }

    public Lesson getLesson(String id) throws LessonNotFoundException {
        Lesson lesson = lessonRepository.findByIdWithModulesAndAbilities(id)
            .orElseThrow(() -> new LessonNotFoundException(id));
        populateOwnerName(lesson);
        return lesson;
    }

    /**
     * Get a lesson with access control.
     * If user doesn't have access, modules are hidden.
     */
    public Lesson getLessonWithAccessControl(String id, String username) throws LessonNotFoundException {
        Lesson lesson = lessonRepository.findByIdWithModulesAndAbilities(id)
            .orElseThrow(() -> new LessonNotFoundException(id));
        populateOwnerName(lesson);
        
        // If user doesn't have access, hide modules
        if (!canAccessLessonContent(username, lesson)) {
            lesson.setModules(new HashSet<>());
        }
        
        return lesson;
    }

    public Lesson updateLesson(String id, List<JsonPatchOperation> changes) throws LessonNotFoundException, JsonPatchException {
        Lesson lesson = lessonRepository.findById(id).orElseThrow(() -> new LessonNotFoundException(id));
            JsonPatch patch = new JsonPatch(changes);
            JsonNode patched = patch.apply(mapper.convertValue(lesson, JsonNode.class));
            Lesson lessonPatched = mapper.convertValue(patched, Lesson.class);
            return lessonRepository.save(lessonPatched);
    }
    
    public void deleteLesson(String id) throws LessonNotFoundException {
        if (lessonRepository.existsById(id)) {
            lessonRepository.deleteById(id);
        } else {
            throw new LessonNotFoundException(id);
        }
    }
}
