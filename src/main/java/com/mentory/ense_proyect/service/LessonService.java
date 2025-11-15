package com.mentory.ense_proyect.service;

import com.mentory.ense_proyect.exception.*;
import com.mentory.ense_proyect.model.Lesson;
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
    private final ObjectMapper mapper;

    @Autowired
    public LessonService(LessonRepository leccionRepository, ObjectMapper mapper) {
        this.lessonRepository = leccionRepository;
        this.mapper=mapper;

        leccionRepository.save(new Lesson("pepe10","Lección de prueba","Esta es una lección de prueba"));
    }

    // CRUD
    public Lesson addLesson(Lesson lesson) throws DuplicatedLessonException {
        if (!lessonRepository.exists(Example.of(lesson))) {
            return lessonRepository.save(lesson);
        } else {
            throw new DuplicatedLessonException(lesson);
        }
    }

    public Page<@NonNull Lesson> getLessons(@Nullable String name, PageRequest page) {
        Example<Lesson> example = Example.of(new Lesson(name, null, null));
        return lessonRepository.findAll(example, page);
    }

    public Lesson getLesson(String id) throws LessonNotFoundException {
        return lessonRepository.findById(id).orElseThrow(() -> new LessonNotFoundException(id));
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
