package com.mentory.ense_proyect.service;

import com.mentory.ense_proyect.exception.*;
import com.mentory.ense_proyect.model.Leccion;
import com.mentory.ense_proyect.repository.LeccionRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;

import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.github.fge.jsonpatch.JsonPatchOperation;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.*;

@Service
public class LeccionService {
    private final LeccionRepository leccionRepository;
    private final ObjectMapper mapper;

    @Autowired
    public LeccionService(LeccionRepository leccionRepository, ObjectMapper mapper) {
        this.leccionRepository = leccionRepository;
        this.mapper=mapper;

        leccionRepository.save(new Leccion("pepe10","Lección de prueba","Esta es una lección de prueba"));
    }

    // CRUD
    public Leccion addLeccion(Leccion leccion) throws DuplicatedLeccionException {
        if (!leccionRepository.exists(Example.of(leccion))) {
            return leccionRepository.save(leccion);
        } else {
            throw new DuplicatedLeccionException(leccion);
        }
    }

    public Page<@NonNull Leccion> getLecciones(@Nullable String nombre, PageRequest page) {
        Example<Leccion> example = Example.of(new Leccion(nombre, null, null));
        return leccionRepository.findAll(example, page);
    }

    public Leccion getLeccion(String id) throws LeccionNotFoundException {
        return leccionRepository.findById(id).orElseThrow(() -> new LeccionNotFoundException(id));
    }

    public Leccion updateLeccion(String id, List<JsonPatchOperation> changes) throws LeccionNotFoundException, JsonPatchException {
        Leccion leccion = leccionRepository.findById(id).orElseThrow(() -> new LeccionNotFoundException(id));
            JsonPatch patch = new JsonPatch(changes);
            JsonNode patched = patch.apply(mapper.convertValue(leccion, JsonNode.class));
            Leccion leccionPatched = mapper.convertValue(patched, Leccion.class);
            return leccionRepository.save(leccionPatched);
    }
    
    public void deleteLeccion(String id) throws LeccionNotFoundException {
        if (leccionRepository.existsById(id)) {
            leccionRepository.deleteById(id);
        } else {
            throw new LeccionNotFoundException(id);
        }
    }
}
