package com.mentory.ense_proyect.controller;

import com.mentory.ense_proyect.exception.DuplicatedModuleException;
import com.mentory.ense_proyect.exception.ModuleNotFoundException;
import com.mentory.ense_proyect.model.Module;
import com.mentory.ense_proyect.service.ModuleService;

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
@RequestMapping("modules")
public class ModuleController {
    ModuleService moduleService;

    @Autowired
    public ModuleController(ModuleService moduleService) {
        this.moduleService = moduleService;
    }

    @GetMapping("{id}")
    public ResponseEntity<Module> getModulo(@PathVariable("id") String id) throws ModuleNotFoundException {
        return ResponseEntity.ok(moduleService.getModule(id));
    }

    @GetMapping
    public ResponseEntity<Page<Module>> getModules(
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

    @PostMapping
    public ResponseEntity<Module> createModule(@RequestBody Module module) throws DuplicatedModuleException {
        Module newModule = moduleService.addModule(module);
        return ResponseEntity
                .created(MvcUriComponentsBuilder
                        .fromMethodName(ModuleController.class, "getModule", module.getId())
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