package com.mentory.ense_proyect.service;

import com.mentory.ense_proyect.exception.*;
import com.mentory.ense_proyect.model.dto.LessonDTO;
import com.mentory.ense_proyect.model.entity.Ability;
import com.mentory.ense_proyect.model.entity.Lesson;
import com.mentory.ense_proyect.repository.AbilityRepository;
import com.mentory.ense_proyect.repository.LessonRepository;

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
    private final ObjectMapper mapper;

    @Autowired
    public LessonService(LessonRepository leccionRepository, AbilityRepository abilityRepository, ObjectMapper mapper) {
        this.lessonRepository = leccionRepository;
        this.abilityRepository = abilityRepository;
        this.mapper=mapper;
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
        
        return lessonRepository.save(lesson);
    }

    public Page<@NonNull Lesson> getLessons(@Nullable String name, PageRequest page) {
        if (name == null || name.isBlank()) {
            // Return all lessons if no name filter
            return lessonRepository.findAll(page);
        }
        // Search by name using repository method
        return lessonRepository.findByNameContainingIgnoreCase(name, page);
    }

    public Lesson getLesson(String id) throws LessonNotFoundException {
        return lessonRepository.findByIdWithModulesAndAbilities(id)
            .orElseThrow(() -> new LessonNotFoundException(id));
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
