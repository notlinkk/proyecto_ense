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
    private final AbilityRepository abilityRepository;
    private final ObjectMapper mapper;

    @Autowired
    public AbilityService(AbilityRepository abilityRepository, ObjectMapper mapper) {
        this.abilityRepository = abilityRepository;
        this.mapper = mapper;
    }

    public Ability addAbility(Ability ability) throws DuplicatedAbilityException {
        if (!abilityRepository.existsById(ability.getName())) {
            return abilityRepository.save(ability);
        } else {
            throw new DuplicatedAbilityException(ability);
        }
    }

    public Page<@NonNull Ability> getAbilities(@Nullable String name, PageRequest page) {
        Example<Ability> example = Example.of(new Ability(name, null));
        return abilityRepository.findAll(example, page);
    }

    public Ability getAbility(String id) throws AbilityNotFoundException {
        return abilityRepository.findById(id).orElseThrow(() -> new AbilityNotFoundException(id));
    }

    public Ability updateAbility(String name, List<JsonPatchOperation> changes) throws AbilityNotFoundException, JsonPatchException {
        Ability ability = abilityRepository.findById(name).orElseThrow(() -> new AbilityNotFoundException(name));
            JsonPatch patch = new JsonPatch(changes);
            JsonNode patched = patch.apply(mapper.convertValue(ability, JsonNode.class));
            Ability updated = mapper.convertValue(patched, Ability.class);
            return abilityRepository.save(updated);
    }

    public void deleteAbility(String id) throws AbilityNotFoundException {
        if (abilityRepository.existsById(id)){
            abilityRepository.deleteById(id);
        } else {
            throw new AbilityNotFoundException(id);
        }

    }
}
