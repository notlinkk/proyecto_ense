package com.mentory.ense_proyect.service;

import com.mentory.ense_proyect.exception.*;
import com.mentory.ense_proyect.model.Suscripcion;
import com.mentory.ense_proyect.repository.SuscripcionRepository;

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
public class SuscripcionService {
    private final SuscripcionRepository suscripcionRepository;
    private final ObjectMapper mapper;

    @Autowired
    public SuscripcionService(SuscripcionRepository suscripcionRepository, ObjectMapper mapper) {
        this.suscripcionRepository = suscripcionRepository;
        this.mapper=mapper;
    }

    // CRUD
    public Suscripcion addSuscripcion(Suscripcion suscripcion) throws DuplicatedSuscripcionException {
        if (!suscripcionRepository.exists(Example.of(suscripcion))) {
            return suscripcionRepository.save(suscripcion);
        } else {
            throw new DuplicatedSuscripcionException(suscripcion);
        }
    }

    public Page<@NonNull Suscripcion> getSuscripciones(@Nullable String id, PageRequest page) {
        Example<Suscripcion> example = Example.of(new Suscripcion(id, null, 0, false, null, null));
        return suscripcionRepository.findAll(example, page);
    }

    public Suscripcion getSuscripcion(String id) throws SuscripcionNotFoundException {
        return suscripcionRepository.findById(id).orElseThrow(() -> new SuscripcionNotFoundException(id));
    }

    public Suscripcion updateSuscripcion(String id, List<JsonPatchOperation> changes) throws SuscripcionNotFoundException, JsonPatchException {
        Suscripcion suscripcion = suscripcionRepository.findById(id).orElseThrow(() -> new SuscripcionNotFoundException(id));
            JsonPatch patch = new JsonPatch(changes);
            JsonNode patched = patch.apply(mapper.convertValue(suscripcion, JsonNode.class));
            Suscripcion suscripcionPatched = mapper.convertValue(patched, Suscripcion.class);
            return suscripcionRepository.save(suscripcionPatched);
    }

    public void deleteSuscripcion(String id) throws SuscripcionNotFoundException {
        if (suscripcionRepository.existsById(id)) {
            suscripcionRepository.deleteById(id);
        } else {
            throw new SuscripcionNotFoundException(id);
        }
    }
}
