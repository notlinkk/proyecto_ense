package com.mentory.ense_proyect.service;

import com.mentory.ense_proyect.exception.*;
import com.mentory.ense_proyect.model.Ability;
import com.mentory.ense_proyect.model.Ability;
import com.mentory.ense_proyect.repository.AbilityRepository;

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
public class AbilityService {
    private final AbilityRepository habilidadRepository;
    private final ObjectMapper mapper;

    @Autowired
    public AbilityService(AbilityRepository habilidadRepository, ObjectMapper mapper) {
        this.habilidadRepository = habilidadRepository;
        this.mapper = mapper;
        if (habilidadRepository.count() == 0) {
            habilidadRepository.saveAll(List.of(
                    new Ability("comunicacion-efectiva", "Capacidad para expresar ideas con claridad y escuchar activamente."),
                    new Ability( "trabajo-en-equipo", "Ability para colaborar con otros hacia un objetivo común."),
                    new Ability( "liderazgo", "Capacidad para guiar, motivar y coordinar a un grupo."),
                    new Ability( "creatividad", "Generación de ideas nuevas o enfoques originales para resolver problemas."),
                    new Ability( "autodisciplina", "Mantener constancia y compromiso con los objetivos."),
                    new Ability( "autoconvicción", "Mantener constancia y compromiso con los objetivos.")
            ));
        }
    }

    // CRUD
    public Ability createAbility(Ability habilidad) throws DuplicatedAbilityException {
        if (!habilidadRepository.exists(Example.of(habilidad))) {
            return habilidadRepository.save(habilidad);
        } else {
            throw new DuplicatedAbilityException(habilidad);
        }
    }

    public Page<@NonNull Ability> getAbilities(@Nullable String nombre, PageRequest page) {
        Example<Ability> example = Example.of(new Ability(nombre, null));
        return habilidadRepository.findAll(example, page);
    }

    public Ability getAbility(String id) throws AbilityNotFoundException {
        return habilidadRepository.findById(id).orElseThrow(() -> new AbilityNotFoundException(id));
    }

    public Ability updateAbility(String nombre, List<JsonPatchOperation> changes) throws AbilityNotFoundException, JsonPatchException {
        Ability habilidad = habilidadRepository.findById(nombre).orElseThrow(() -> new AbilityNotFoundException(nombre));
            JsonPatch patch = new JsonPatch(changes);
            JsonNode patched = patch.apply(mapper.convertValue(habilidad, JsonNode.class));
            Ability updated = mapper.convertValue(patched, Ability.class);
            return habilidadRepository.save(updated);
    }

    public void deleteAbility(String id) throws AbilityNotFoundException {
        if (habilidadRepository.existsById(id)){
            habilidadRepository.deleteById(id);
        } else {
            throw new AbilityNotFoundException(id);
        }

    }
}
