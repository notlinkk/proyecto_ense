package com.mentory.ense_proyect.controller;

import com.mentory.ense_proyect.exception.DuplicatedHabilidadException;
import com.mentory.ense_proyect.exception.HabilidadNotFoundException;
import com.mentory.ense_proyect.model.Habilidad;
import com.mentory.ense_proyect.service.HabilidadService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import com.github.fge.jsonpatch.JsonPatchOperation;
import com.github.fge.jsonpatch.JsonPatchException;

import java.util.*;

@RestController
@RequestMapping("habilidades")
public class HabilidadController {
    HabilidadService habilidadService;

    @Autowired
    public HabilidadController(HabilidadService habilidadService) {
        this.habilidadService = habilidadService;
    }

    @GetMapping("{id}")
    public ResponseEntity <Habilidad> getHabilidad(@PathVariable("id") String id) throws HabilidadNotFoundException {
        return ResponseEntity.ok(habilidadService.getHabilidad(id));
    }

    @GetMapping
    public ResponseEntity<Set<Habilidad>> getHabilidades() {
        Set<Habilidad> habilidades = habilidadService.getHabilidades();
        if (habilidades.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(habilidades);
    }

    @PostMapping
    public ResponseEntity<Habilidad> createHabilidad(@RequestBody Habilidad habilidad) throws DuplicatedHabilidadException {
            Habilidad nuevaHabilidad = habilidadService.createHabilidad(habilidad);
            return ResponseEntity
                    .created(MvcUriComponentsBuilder
                            .fromMethodName(HabilidadController.class, "getHabilidad", habilidad.nombre())
                            .build()
                            .toUri())
                    .body(nuevaHabilidad);
    }

    @DeleteMapping({"{id}"})
    public ResponseEntity<Void> deleteHabilidad(@PathVariable("id") String id) throws HabilidadNotFoundException {
        habilidadService.deleteHabilidad(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("{id}")
    public ResponseEntity<Habilidad> updateHabilidad(
            @PathVariable("id") String id,
            @RequestBody List<JsonPatchOperation> changes
            ) throws HabilidadNotFoundException, JsonPatchException {
        return ResponseEntity.ok(habilidadService.updateHabilidad(id, changes));
    }
}


