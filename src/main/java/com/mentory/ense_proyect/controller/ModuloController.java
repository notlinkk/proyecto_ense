package com.mentory.ense_proyect.controller;

import com.mentory.ense_proyect.exception.DuplicatedModuloException;
import com.mentory.ense_proyect.exception.ModuloNotFoundException;
import com.mentory.ense_proyect.model.Modulo;
import com.mentory.ense_proyect.service.ModuloService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import com.github.fge.jsonpatch.JsonPatchOperation;
import com.github.fge.jsonpatch.JsonPatchException;

import java.util.*;


@RestController
@RequestMapping("modulos")
public class ModuloController {
    ModuloService moduloService;

    @Autowired
    public ModuloController(ModuloService moduloService) {
        this.moduloService = moduloService;
    }

    @GetMapping("{id}")
    public ResponseEntity<Modulo> getModulo(@PathVariable("id") String id) throws ModuloNotFoundException {
        return ResponseEntity.ok(moduloService.getModulo(id));
    }

    @GetMapping
    public ResponseEntity<Set<Modulo>> getModulos() {
        Set<Modulo> modulos = moduloService.getModulos();
        if (modulos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(modulos);
    }

    @PostMapping
    public ResponseEntity<Modulo> createModulo(@RequestBody Modulo modulo) throws DuplicatedModuloException {
        Modulo newModulo = moduloService.addModulo(modulo);
        return ResponseEntity
                .created(MvcUriComponentsBuilder
                        .fromMethodName(ModuloController.class, "getModulo", modulo.getId())
                        .build()
                        .toUri())
                .body(newModulo);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteModulo(@PathVariable("id") String id) throws ModuloNotFoundException {
        moduloService.deleteModulo(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("{id}")
    public ResponseEntity<Modulo> updateModulo(
            @PathVariable("id") String id,
            @RequestBody List<JsonPatchOperation> changes
        ) throws ModuloNotFoundException, JsonPatchException {
        return ResponseEntity.ok(moduloService.updateModulo(id, changes));
    }
}