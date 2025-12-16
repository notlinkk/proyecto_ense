package com.mentory.ense_proyect.service;

import com.mentory.ense_proyect.exception.*;
import com.mentory.ense_proyect.model.entity.Ability;
import com.mentory.ense_proyect.repository.AbilityRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.*;

@Service
public class AbilityService {
    private final AbilityRepository abilityRepository;

    @Autowired
    public AbilityService(AbilityRepository abilityRepository) {
        this.abilityRepository = abilityRepository;
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

    public Ability updateAbility(String name, Map<String, Object> changes) throws AbilityNotFoundException {
        Ability ability = abilityRepository.findById(name).orElseThrow(() -> new AbilityNotFoundException(name));
        BeanWrapper wrapper = new BeanWrapperImpl(ability);
        changes.forEach((key, value) -> {
            if (wrapper.isWritableProperty(key)) {
                wrapper.setPropertyValue(key, value);
            }
        });
        return abilityRepository.save(ability);
    }

    public void deleteAbility(String id) throws AbilityNotFoundException {
        if (abilityRepository.existsById(id)){
            abilityRepository.deleteById(id);
        } else {
            throw new AbilityNotFoundException(id);
        }

    }
}
