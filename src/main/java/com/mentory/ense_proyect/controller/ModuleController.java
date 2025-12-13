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
    public ResponseEntity<Module> getModuloV0(@PathVariable("id") String id) throws ModuleNotFoundException {
        return ResponseEntity.ok(moduleService.getModule(id));
    }

    @GetMapping(path="{id}", version = "1")
    public ResponseEntity<EntityModel<Module>> getModuleV1(@PathVariable("id") String id) throws ModuleNotFoundException {
        EntityModel<Module> module = EntityModel.of(moduleService.getModule(id));
        module.add(
            entityLinks.linkToItemResource(Module.class, module).withSelfRel(),
            entityLinks.linkToCollectionResource(Module.class).withRel(IanaLinkRelations.COLLECTION),
            entityLinks.linkToItemResource(Module.class, module).withRel("delete").withType("DELETE")
        );
        return ResponseEntity.ok(module);
    }

    @GetMapping( version = "0")
    public ResponseEntity<Page<Module>> getModulesV0(
            @RequestParam(value="nombre", required=false) String nombre,
            @RequestParam(value="page", required=false, defaultValue="0") int page,
            @RequestParam(value="size", required=false, defaultValue="2") int pagesize,
            @RequestParam(value="sort", required=false, defaultValue="") List<String> sort
    ) {
        Page<Module> modules = moduleService.getModules(
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

        if (modules.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(modules);
    }

    @GetMapping(version = "1")
    public ResponseEntity<PagedModel<Module>> getModulesV1(
            @RequestParam(value="nombre", required=false) String nombre,
            @RequestParam(value="page", required=false, defaultValue="0") int page,
            @RequestParam(value="size", required=false, defaultValue="2") int pagesize,
            @RequestParam(value="sort", required=false, defaultValue="") List<String> sort
    ) {
        Page<Module> modules = moduleService.getModules(
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
                        .getModulesV1(nombre, page - 1, pagesize, sort))
                        .withRel(IanaLinkRelations.PREVIOUS)
            );
        }

        // NEXT
        if (modules.hasNext()) {
            response.add(
                linkTo(methodOn(ModuleController.class)
                        .getModulesV1(nombre, page + 1, pagesize, sort))
                        .withRel(IanaLinkRelations.NEXT)
            );
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping
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
    public ResponseEntity<Void> deleteModule(@PathVariable("id") String id) throws ModuleNotFoundException {
        moduleService.deleteModule(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("{id}")
    public ResponseEntity<Module> updateModule(
            @PathVariable("id") String id,
            @RequestBody List<JsonPatchOperation> changes
        ) throws ModuleNotFoundException, JsonPatchException {
        return ResponseEntity.ok(moduleService.updateModule(id, changes));
    }
}