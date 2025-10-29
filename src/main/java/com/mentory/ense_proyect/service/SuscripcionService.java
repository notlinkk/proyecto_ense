package com.mentory.ense_proyect.service;

import com.mentory.ense_proyect.exception.*;
import com.mentory.ense_proyect.model.Suscripcion;
import com.mentory.ense_proyect.repository.SuscripcionRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.github.fge.jsonpatch.JsonPatchOperation;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;

@Service
public class SuscripcionService {
    private final SuscripcionRepository suscripcionRepository;
    private final ObjectMapper mapper;

    @Autowired
    public SuscripcionService(SuscripcionRepository suscripcionRepository, ObjectMapper mapper) {
        this.suscripcionRepository = suscripcionRepository;
        this.mapper=mapper;

        suscripcionRepository.save(new Suscripcion("15/10/2023","15/10/2024", 20.0, true,"us10" ,"l1"));
    }

    // CRUD
    public Suscripcion addSuscripcion(Suscripcion suscripcion) throws DuplicatedSuscripcionException {
        if (!suscripcionRepository.exists(Example.of(suscripcion))) {
            return suscripcionRepository.save(suscripcion);
        } else {
            throw new DuplicatedSuscripcionException(suscripcion);
        }
    }

    public Set<Suscripcion> getSuscripciones(){
        return new HashSet<>(suscripcionRepository.findAll());
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
