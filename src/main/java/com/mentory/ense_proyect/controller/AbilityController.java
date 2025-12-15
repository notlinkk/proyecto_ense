package com.mentory.ense_proyect.controller;

import com.mentory.ense_proyect.exception.DuplicatedAbilityException;
import com.mentory.ense_proyect.model.entity.Ability;
import com.mentory.ense_proyect.exception.AbilityNotFoundException;
import com.mentory.ense_proyect.service.AbilityService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import org.springframework.hateoas.*;
import org.springframework.hateoas.server.EntityLinks;
import org.springframework.hateoas.server.ExposesResourceFor;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

import com.github.fge.jsonpatch.JsonPatchOperation;
import com.github.fge.jsonpatch.JsonPatchException;

import org.springframework.security.access.prepost.PreAuthorize;

import java.util.*;

@RestController
@RequestMapping("abilities")
@ExposesResourceFor(Ability.class)
public class AbilityController {
    private final AbilityService abilityService;
    private EntityLinks entityLinks;

    @Autowired
    public AbilityController(AbilityService abilityService, EntityLinks entityLinks) {
        this.abilityService = abilityService;
        this.entityLinks = entityLinks;
    }

        @GetMapping(path="{id}", headers = "API-Version=0")
    @PreAuthorize("hasAuthority('abilities:read')")
    public ResponseEntity <Ability> getAbilityV0(@PathVariable("id") String id) throws AbilityNotFoundException {
        return ResponseEntity.ok(abilityService.getAbility(id));
    }

        @GetMapping(path="{id}", headers = "API-Version=1")
    @PreAuthorize("hasAuthority('abilities:read')")
    public ResponseEntity <EntityModel<Ability>> getAbilityV1(@PathVariable("id") String id) throws AbilityNotFoundException {
        EntityModel<Ability> ability = EntityModel.of(abilityService.getAbility(id));
        ability.add(
                entityLinks.linkToItemResource(Ability.class, ability.getContent().getName()).withSelfRel(),
                entityLinks.linkToCollectionResource(Ability.class).withRel(IanaLinkRelations.COLLECTION),
                entityLinks.linkToItemResource(Ability.class, ability.getContent().getName()).withRel("delete").withType("DELETE")
        );
        return ResponseEntity.ok(ability);
    }

    @GetMapping(headers = "API-Version=0")
    @PreAuthorize("hasAuthority('abilities:read')")
    public ResponseEntity<Page<Ability>> getAbilitiesV0(
            @RequestParam(value="nombre", required=false) String nombre,
            @RequestParam(value="page", required=false, defaultValue="0") int page,
            @RequestParam(value="size", required=false, defaultValue="2") int pagesize,
            @RequestParam(value="sort", required=false, defaultValue="") List<String> sort
    ) {
        Page<Ability> abilities = abilityService.getAbilities(
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

        if (abilities.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(abilities);
    }

        @GetMapping(headers = "API-Version=1")
    @PreAuthorize("hasAuthority('abilities:read')")
    public ResponseEntity<PagedModel<Ability>> getAbilitiesV1(
            @RequestParam(value="nombre", required=false) String nombre,
            @RequestParam(value="page", required=false, defaultValue="0") int page,
            @RequestParam(value="size", required=false, defaultValue="2") int pagesize,
            @RequestParam(value="sort", required=false, defaultValue="") List<String> sort
    ) {
        Page<Ability> abilities = abilityService.getAbilities(
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

        if (abilities.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        PagedModel<Ability> response= PagedModel.of(
            abilities.getContent(),
            new PagedModel.PageMetadata(
                abilities.getSize(),
                abilities.getNumber(),
                abilities.getTotalElements(),
                abilities.getTotalPages()
            )
        );

        response.add( entityLinks.linkToCollectionResource(Ability.class).withSelfRel() );

        // PREVIOUS
        if (abilities.hasPrevious()) {
            response.add(
                    linkTo(methodOn(AbilityController.class)
                            .getAbilitiesV1(nombre, page - 1, pagesize, sort))
                            .withRel(IanaLinkRelations.PREV)
            );
        }

        // NEXT
        if (abilities.hasNext()) {
            response.add(
                    linkTo(methodOn(AbilityController.class)
                            .getAbilitiesV1(nombre, page + 1, pagesize, sort))
                            .withRel(IanaLinkRelations.NEXT)
            );
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('abilities:write')")
    public ResponseEntity<Ability> createAbility(@RequestBody Ability Ability) throws DuplicatedAbilityException {
            Ability nuevaAbility = abilityService.addAbility(Ability);
            return ResponseEntity
                    .created(MvcUriComponentsBuilder
                            .fromMethodName(AbilityController.class, "getAbilityV1", nuevaAbility.getName())
                            .build()
                            .toUri())
                    .body(nuevaAbility);
    }

    @DeleteMapping({"{id}"})
    @PreAuthorize("hasAuthority('abilities:delete')")
    public ResponseEntity<Void> deleteAbility(@PathVariable("id") String id) throws AbilityNotFoundException {
        abilityService.deleteAbility(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("{id}")
    @PreAuthorize("hasAuthority('abilities:update')")
    public ResponseEntity<Ability> updateAbility(
            @PathVariable("id") String id,
            @RequestBody List<JsonPatchOperation> changes
            ) throws AbilityNotFoundException, JsonPatchException {
        return ResponseEntity.ok(abilityService.updateAbility(id, changes));
    }
}


