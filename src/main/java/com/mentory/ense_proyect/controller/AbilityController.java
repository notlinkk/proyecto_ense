package com.mentory.ense_proyect.controller;

import com.mentory.ense_proyect.exception.DuplicatedAbilityException;
import com.mentory.ense_proyect.exception.AbilityNotFoundException;
import com.mentory.ense_proyect.model.Ability;
import com.mentory.ense_proyect.service.AbilityService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import com.github.fge.jsonpatch.JsonPatchOperation;
import com.github.fge.jsonpatch.JsonPatchException;

import java.util.*;

@RestController
@RequestMapping("abilities")
public class AbilityController {
    private final AbilityService abilityService;

    @Autowired
    public AbilityController(AbilityService abilityService) {
        this.abilityService = abilityService;
    }

    @GetMapping("{id}")
    public ResponseEntity <Ability> getAbility(@PathVariable("id") String id) throws AbilityNotFoundException {
        return ResponseEntity.ok(abilityService.getAbility(id));
    }

    @GetMapping
    public ResponseEntity<Page<Ability>> getAbilityes(
            @RequestParam(value="nombre", required=false) String nombre,
            @RequestParam(value="page", required=false, defaultValue="0") int page,
            @RequestParam(value="size", required=false, defaultValue="2") int pagesize,
            @RequestParam(value="sort", required=false, defaultValue="") List<String> sort
    ) {
        Page<Ability> Abilityes = abilityService.getAbilities(
                nombre,
                PageRequest.of(
                        page, pagesize,
                        Sort.by(sort.stream()
                                .map(key -> key.startsWith("-") ?
                                        Sort.Order.desc(key.substring(1)) :
                                        Sort.Order.asc(key))
                                .toList()
                        )
                )
        );

        if (Abilityes.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(Abilityes);
    }

    @PostMapping
    public ResponseEntity<Ability> createAbility(@RequestBody Ability Ability) throws DuplicatedAbilityException {
            Ability nuevaAbility = abilityService.addAbility(Ability);
            return ResponseEntity
                    .created(MvcUriComponentsBuilder
                            .fromMethodName(AbilityController.class, "getAbility", Ability.getName())
                            .build()
                            .toUri())
                    .body(nuevaAbility);
    }

    @DeleteMapping({"{id}"})
    public ResponseEntity<Void> deleteAbility(@PathVariable("id") String id) throws AbilityNotFoundException {
        abilityService.deleteAbility(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("{id}")
    public ResponseEntity<Ability> updateAbility(
            @PathVariable("id") String id,
            @RequestBody List<JsonPatchOperation> changes
            ) throws AbilityNotFoundException, JsonPatchException {
        return ResponseEntity.ok(abilityService.updateAbility(id, changes));
    }
}


