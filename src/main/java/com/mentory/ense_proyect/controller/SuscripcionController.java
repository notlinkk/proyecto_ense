package com.mentory.ense_proyect.controller;

import com.mentory.ense_proyect.exception.DuplicatedSuscripcionException;
import com.mentory.ense_proyect.exception.SuscripcionNotFoundException;
import com.mentory.ense_proyect.model.Suscripcion;
import com.mentory.ense_proyect.service.SuscripcionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import com.github.fge.jsonpatch.JsonPatchOperation;
import com.github.fge.jsonpatch.JsonPatchException;

import java.util.*;

@RestController
@RequestMapping("suscripciones")
public class SuscripcionController {
    SuscripcionService suscripcionService;

    @Autowired
    public SuscripcionController(SuscripcionService suscripcionService) {
        this.suscripcionService = suscripcionService;
    }

    @GetMapping("{id}")
    public ResponseEntity<Suscripcion> getModulo(@PathVariable("id") String id) throws SuscripcionNotFoundException{
        return ResponseEntity.ok(suscripcionService.getSuscripcion(id));
    }

    @GetMapping
    public ResponseEntity<Set<Suscripcion>> getModulos() {
        Set<Suscripcion> suscripcion = suscripcionService.getSuscripciones();
        if (suscripcion == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(suscripcion);
    }

    @PostMapping
    public ResponseEntity<Suscripcion> createSuscipcion(@RequestBody Suscripcion suscripcion) throws DuplicatedSuscripcionException {
            Suscripcion nuevaSuscripcion = suscripcionService.addSuscripcion(suscripcion);
            return ResponseEntity
                    .created(MvcUriComponentsBuilder
                            .fromMethodName(SuscripcionController.class, "getModulo", suscripcion.getId())
                            .build()
                            .toUri())
                    .body(nuevaSuscripcion);
    }

    @PatchMapping("{id}")
    public ResponseEntity<Suscripcion> updateSuscripcion(
            @PathVariable("id") String id,
            @RequestBody List<JsonPatchOperation> changes
    ) throws SuscripcionNotFoundException, JsonPatchException {
        return ResponseEntity.ok(suscripcionService.updateSuscripcion(id, changes));
    }

    @DeleteMapping({"{id}"})
    public ResponseEntity<Void> deleteSuscripcion(@PathVariable("id") String id) throws
            SuscripcionNotFoundException {
        suscripcionService.deleteSuscripcion(id);
        return ResponseEntity.noContent().build();
    }





}