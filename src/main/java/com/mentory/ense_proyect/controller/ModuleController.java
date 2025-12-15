package com.mentory.ense_proyect.controller;

import com.mentory.ense_proyect.exception.DuplicatedModuleException;
import com.mentory.ense_proyect.exception.LessonNotFoundException;
import com.mentory.ense_proyect.exception.ModuleNotFoundException;
import com.mentory.ense_proyect.model.dto.ModuleDTO;
import com.mentory.ense_proyect.model.entity.Module;
import com.mentory.ense_proyect.service.ModuleService;

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
import org.springframework.security.core.Authentication;

import java.util.*;


@RestController
@RequestMapping("modules")
@ExposesResourceFor(Module.class)
public class ModuleController {
    ModuleService moduleService;
    private EntityLinks entityLinks;

    @Autowired
    public ModuleController(ModuleService moduleService, EntityLinks entityLinks) {
        this.moduleService = moduleService;
        this.entityLinks = entityLinks;
    }

    @GetMapping(path="{id}", version = "0")
    @PreAuthorize("hasAuthority('modules:read')")
    public ResponseEntity<Module> getModuloV0(@PathVariable("id") String id, Authentication authentication) throws ModuleNotFoundException {
        String username = authentication.getName();
        return ResponseEntity.ok(moduleService.getModuleWithAccessControl(id, username));
    }

    @GetMapping(path="{id}", version = "1")
    @PreAuthorize("hasAuthority('modules:read')")
    public ResponseEntity<EntityModel<Module>> getModuleV1(@PathVariable("id") String id, Authentication authentication) throws ModuleNotFoundException {
        String username = authentication.getName();
        EntityModel<Module> module = EntityModel.of(moduleService.getModuleWithAccessControl(id, username));
        module.add(
            entityLinks.linkToItemResource(Module.class, module).withSelfRel(),
            entityLinks.linkToCollectionResource(Module.class).withRel(IanaLinkRelations.COLLECTION),
            entityLinks.linkToItemResource(Module.class, module).withRel("delete").withType("DELETE")
        );
        return ResponseEntity.ok(module);
    }

    @GetMapping( version = "0")
    @PreAuthorize("hasAuthority('modules:read')")
    public ResponseEntity<Page<Module>> getModulesV0(
            @RequestParam(value="nombre", required=false) String nombre,
            @RequestParam(value="page", required=false, defaultValue="0") int page,
            @RequestParam(value="size", required=false, defaultValue="10") int pagesize,
            @RequestParam(value="sort", required=false, defaultValue="") List<String> sort,
            Authentication authentication
    ) {
        String username = authentication.getName();
        Page<Module> modules = moduleService.getModulesWithAccessControl(
                nombre,
                username,
                PageRequest.of(page, pagesize)
        );

        if (modules.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(modules);
    }

    @GetMapping(version = "1")
    @PreAuthorize("hasAuthority('modules:read')")
    public ResponseEntity<PagedModel<Module>> getModulesV1(
            @RequestParam(value="nombre", required=false) String nombre,
            @RequestParam(value="page", required=false, defaultValue="0") int page,
            @RequestParam(value="size", required=false, defaultValue="10") int pagesize,
            @RequestParam(value="sort", required=false, defaultValue="") List<String> sort,
            Authentication authentication
    ) {
        String username = authentication.getName();
        Page<Module> modules = moduleService.getModulesWithAccessControl(
                nombre,
                username,
                PageRequest.of(page, pagesize)
        );

        if (modules.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        PagedModel<Module> response= PagedModel.of(
            modules.getContent(),
            new PagedModel.PageMetadata(
                modules.getSize(),
                modules.getNumber(),
                modules.getTotalElements(),
                modules.getTotalPages()
            )
        );

        response.add( entityLinks.linkToCollectionResource(Module.class).withSelfRel() );

        // PREVIOUS
        if (modules.hasPrevious()) {
            response.add(
                linkTo(methodOn(ModuleController.class)
                        .getModulesV1(nombre, page - 1, pagesize, sort, authentication))
                        .withRel(IanaLinkRelations.PREVIOUS)
            );
        }

        // NEXT
        if (modules.hasNext()) {
            response.add(
                linkTo(methodOn(ModuleController.class)
                        .getModulesV1(nombre, page + 1, pagesize, sort, authentication))
                        .withRel(IanaLinkRelations.NEXT)
            );
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('modules:write')")
    public ResponseEntity<Module> createModule(@RequestBody ModuleDTO moduleDTO) throws LessonNotFoundException {
        Module newModule = moduleService.createModule(moduleDTO);
        return ResponseEntity
                .created(MvcUriComponentsBuilder
                        .fromMethodName(ModuleController.class, "getModuleV1", newModule.getId())
                        .build()
                        .toUri())
                .body(newModule);
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasAuthority('modules:delete')")
    public ResponseEntity<Void> deleteModule(@PathVariable("id") String id) throws ModuleNotFoundException {
        moduleService.deleteModule(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("{id}")
    @PreAuthorize("hasAuthority('modules:update')")
    public ResponseEntity<Module> updateModule(
            @PathVariable("id") String id,
            @RequestBody List<JsonPatchOperation> changes
        ) throws ModuleNotFoundException, JsonPatchException {
        return ResponseEntity.ok(moduleService.updateModule(id, changes));
    }
}