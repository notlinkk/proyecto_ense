package com.mentory.ense_proyect.controller;

import com.mentory.ense_proyect.exception.LeccionNotFoundException;
import com.mentory.ense_proyect.exception.DuplicatedLeccionException;
import com.mentory.ense_proyect.model.Leccion;
import com.mentory.ense_proyect.repository.LeccionRepository;
import com.mentory.ense_proyect.service.LeccionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.jspecify.annotations.NonNull;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.github.fge.jsonpatch.JsonPatchOperation;


import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("lecciones")
public class LeccionController {
    private final LeccionRepository leccionRepository;
    LeccionService leccionService;

    @Autowired
    public LeccionController(LeccionService leccionService, LeccionRepository leccionRepository) {
        this.leccionService = leccionService;
        this.leccionRepository = leccionRepository;
    }

    @GetMapping("{id}")
    public ResponseEntity getLeccion(@PathVariable("id") String id) throws LeccionNotFoundException {
        return ResponseEntity.ok(leccionService.getLeccion(id));
    }

    @GetMapping
    public ResponseEntity<Set<Leccion>> getLecciones() {
        Set<Leccion> lecciones = leccionService.getLecciones();
        if (lecciones.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(lecciones);
    }

    @PostMapping
    public ResponseEntity<Leccion> createLeccion(@RequestBody Leccion leccion) throws DuplicatedLeccionException {
        Leccion newLeccion = leccionService.addLeccion(leccion);
        return ResponseEntity
                .created(MvcUriComponentsBuilder
                        .fromMethodName(LeccionController.class, "getLeccion", leccion.getId())
                        .build()
                        .toUri())
                .body(newLeccion);
    }

    @DeleteMapping({"{id}"})
    public ResponseEntity<Void> deleteLeccion(@PathVariable("id") String id) throws LeccionNotFoundException {
        leccionService.deleteLeccion(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("{id}" )
    public ResponseEntity<Leccion> updateLeccion(
            @PathVariable("id") String id,
            @RequestBody List<JsonPatchOperation> changes
    ) throws LeccionNotFoundException, JsonPatchException {
        return ResponseEntity.ok(leccionService.updateLeccion(id, changes));
    }

}
