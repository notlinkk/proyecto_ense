package com.mentory.ense_proyect.service;

import com.mentory.ense_proyect.exception.*;
import com.mentory.ense_proyect.model.Habilidad;
import com.mentory.ense_proyect.repository.HabilidadRepository;

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
public class HabilidadService {
    private final HabilidadRepository habilidadRepository;
    private final ObjectMapper mapper;

    @Autowired
    public HabilidadService(HabilidadRepository habilidadRepository, ObjectMapper mapper) {
        this.habilidadRepository = habilidadRepository;
        this.mapper = mapper;
        if (habilidadRepository.count() == 0) {
            habilidadRepository.saveAll(List.of(
                    new Habilidad("comunicacion-efectiva", "Capacidad para expresar ideas con claridad y escuchar activamente."),
                    new Habilidad( "trabajo-en-equipo", "Habilidad para colaborar con otros hacia un objetivo común."),
                    new Habilidad( "liderazgo", "Capacidad para guiar, motivar y coordinar a un grupo."),
                    new Habilidad( "creatividad", "Generación de ideas nuevas o enfoques originales para resolver problemas."),
                    new Habilidad( "autodisciplina", "Mantener constancia y compromiso con los objetivos."),
                    new Habilidad( "autoconvicción", "Mantener constancia y compromiso con los objetivos.")
            ));
        }
    }

    // CRUD
    public Habilidad createHabilidad(Habilidad habilidad) throws DuplicatedHabilidadException {
        if (!habilidadRepository.exists(Example.of(habilidad))) {
            return habilidadRepository.save(habilidad);
        } else {
            throw new DuplicatedHabilidadException(habilidad);
        }
    }

    public Page<@NonNull Habilidad> getHabilidades(@Nullable String nombre, PageRequest page) {
        Example<Habilidad> example = Example.of(new Habilidad(nombre, null));
        return habilidadRepository.findAll(example, page);
    }

    public Habilidad getHabilidad(String id) throws HabilidadNotFoundException {
        return habilidadRepository.findById(id).orElseThrow(() -> new HabilidadNotFoundException(id));
    }

    public Habilidad updateHabilidad(String nombre, List<JsonPatchOperation> changes) throws HabilidadNotFoundException, JsonPatchException {
        Habilidad habilidad = habilidadRepository.findById(nombre).orElseThrow(() -> new HabilidadNotFoundException(nombre));
            JsonPatch patch = new JsonPatch(changes);
            JsonNode patched = patch.apply(mapper.convertValue(habilidad, JsonNode.class));
            Habilidad updated = mapper.convertValue(patched, Habilidad.class);
            return habilidadRepository.save(updated);
    }

    public void deleteHabilidad(String id) throws HabilidadNotFoundException {
        if (habilidadRepository.existsById(id)){
            habilidadRepository.deleteById(id);
        } else {
            throw new HabilidadNotFoundException(id);
        }

    }
}
