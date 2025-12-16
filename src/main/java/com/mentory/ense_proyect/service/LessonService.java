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

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.*;

@Service
public class LessonService {
    private final LessonRepository lessonRepository;
    private final AbilityRepository abilityRepository;
    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;

    @Autowired
    public LessonService(LessonRepository leccionRepository, AbilityRepository abilityRepository, 
                         UserRepository userRepository, SubscriptionRepository subscriptionRepository) {
        this.lessonRepository = leccionRepository;
        this.abilityRepository = abilityRepository;
        this.userRepository = userRepository;
        this.subscriptionRepository = subscriptionRepository;
    }

    /**
     * Check if user can access lesson content (modules).
     * User can access if: is owner, has active subscription, or is ADMIN.
     */
    public boolean canAccessLessonContent(String username, Lesson lesson) {
        // Admin can always access
        if (isAdmin(username)) {
            return true;
        }
        // Owner can always access
        if (lesson.getOwnerId().equals(username)) {
            return true;
        }
        // Check for active subscription
        return subscriptionRepository.findByBuyerUsernameAndLessonIdAndActiveTrue(username, lesson.getId()).isPresent();
    }

    /**
     * Check if user has ADMIN role.
     */
    private boolean isAdmin(String username) {
        return userRepository.findById(username)
            .map(user -> user.getRoles().stream()
                .anyMatch(role -> "ADMIN".equals(role.getRolename())))
            .orElse(false);
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

    /**
     * Populates the moduleCount and totalDuration transient fields of a lesson.
     * This should be called BEFORE hiding modules for access control.
     */
    private void populateModuleStats(Lesson lesson) {
        Set<com.mentory.ense_proyect.model.entity.Module> modules = lesson.getModules();
        if (modules != null) {
            lesson.setModuleCount(modules.size());
            int totalDuration = modules.stream()
                .mapToInt(com.mentory.ense_proyect.model.entity.Module::getDuration)
                .sum();
            lesson.setTotalDuration(totalDuration);
        } else {
            lesson.setModuleCount(0);
            lesson.setTotalDuration(0);
        }
    }

    public Lesson getLesson(String id) throws LessonNotFoundException {
        Lesson lesson = lessonRepository.findByIdWithModulesAndAbilities(id)
            .orElseThrow(() -> new LessonNotFoundException(id));
        populateOwnerName(lesson);
        populateModuleStats(lesson);
        return lesson;
    }

    /**
     * Get a lesson with access control.
     * If user doesn't have access, modules are hidden but stats are preserved.
     */
    public Lesson getLessonWithAccessControl(String id, String username) throws LessonNotFoundException {
        Lesson lesson = lessonRepository.findByIdWithModulesAndAbilities(id)
            .orElseThrow(() -> new LessonNotFoundException(id));
        populateOwnerName(lesson);
        
        // Calculate module stats BEFORE potentially hiding modules
        populateModuleStats(lesson);
        
        // If user doesn't have access, hide modules (but stats are already set)
        if (!canAccessLessonContent(username, lesson)) {
            lesson.setModules(new HashSet<>());
        }
        
        return lesson;
    }

    public Lesson updateLesson(String id, Map<String, Object> changes) throws LessonNotFoundException {
        Lesson lesson = lessonRepository.findById(id).orElseThrow(() -> new LessonNotFoundException(id));
        BeanWrapper wrapper = new BeanWrapperImpl(lesson);
        changes.forEach((key, value) -> {
            if (wrapper.isWritableProperty(key)) {
                wrapper.setPropertyValue(key, value);
            }
        });
        return lessonRepository.save(lesson);
    }
    
    public void deleteLesson(String id) throws LessonNotFoundException {
        if (lessonRepository.existsById(id)) {
            lessonRepository.deleteById(id);
        } else {
            throw new LessonNotFoundException(id);
        }
    }
}
