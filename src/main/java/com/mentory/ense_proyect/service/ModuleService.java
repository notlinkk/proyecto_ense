package com.mentory.ense_proyect.service;

import com.mentory.ense_proyect.exception.*;
import com.mentory.ense_proyect.model.dto.ModuleDTO;
import com.mentory.ense_proyect.model.entity.Lesson;
import com.mentory.ense_proyect.model.entity.Module;
import com.mentory.ense_proyect.repository.LessonRepository;
import com.mentory.ense_proyect.repository.ModuleRepository;

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
public class ModuleService {
    private final ModuleRepository moduleRepository;
    private final LessonRepository lessonRepository;
    private final ObjectMapper mapper;

    @Autowired
    public ModuleService(ModuleRepository moduleRepository, LessonRepository lessonRepository, ObjectMapper mapper) {
        this.moduleRepository=moduleRepository;
        this.lessonRepository=lessonRepository;
        this.mapper=mapper;
    }

    // CRUD
    public Module addModule(Module module) throws DuplicatedModuleException {
        if (!moduleRepository.exists(Example.of(module))) {
            return moduleRepository.save(module);
        } else {
            throw new DuplicatedModuleException(module);
        }
    }

    /**
     * Crea un módulo a partir de un DTO.
     * Genera un ID único y busca la lección asociada.
     */
    public Module createModule(ModuleDTO dto) throws LessonNotFoundException {
        Lesson lesson = lessonRepository.findById(dto.lessonId())
                .orElseThrow(() -> new LessonNotFoundException(dto.lessonId()));
        
        Module module = new Module(dto.title(), dto.description(), dto.content(), dto.duration(), dto.position());
        module.setId(UUID.randomUUID().toString());
        module.setLesson(lesson);
        return moduleRepository.save(module);
    }

    public Page<@NonNull Module> getModules(@Nullable String name, PageRequest page) {
        Example<Module> example = Example.of(new Module(name, null, null,0,0));
        return moduleRepository.findAll(example, page);
    }

    public Module getModule(String id) throws ModuleNotFoundException {
        return moduleRepository.findById(id).orElseThrow(() -> new ModuleNotFoundException(id));
    }

    public Module updateModule(String name, List<JsonPatchOperation> changes) throws ModuleNotFoundException, JsonPatchException {
        Module module = moduleRepository.findById(name).orElseThrow(() -> new ModuleNotFoundException(name));
        JsonPatch patch = new JsonPatch(changes);
        JsonNode patched = patch.apply(mapper.convertValue(module, JsonNode.class));
        Module updated = mapper.convertValue(patched, Module.class);
        return moduleRepository.save(updated);
    }

    public void deleteModule(String id) throws ModuleNotFoundException {
        if(moduleRepository.existsById(id)){
            moduleRepository.deleteById(id);
        } else {
            throw new ModuleNotFoundException(id);
        }
    }

}
