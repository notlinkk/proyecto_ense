package com.mentory.ense_proyect.controller;

import com.mentory.ense_proyect.exception.DuplicatedSuscripcionException;
import com.mentory.ense_proyect.exception.SuscripcionNotFoundException;
import com.mentory.ense_proyect.model.Suscripcion;
import com.mentory.ense_proyect.service.SuscripcionService;

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
@RequestMapping("suscripciones")
public class SuscripcionController {
    SuscripcionService suscripcionService;

    @Autowired
    public SuscripcionController(SuscripcionService suscripcionService) {
        this.suscripcionService = suscripcionService;
    }

    @GetMapping("{id}")
    public ResponseEntity<Suscripcion> getSuscripcion(@PathVariable("id") String id) throws SuscripcionNotFoundException{
        return ResponseEntity.ok(suscripcionService.getSuscripcion(id));
    }

    @GetMapping
    public ResponseEntity<Page<Suscripcion>> getSuscripciones(
            @RequestParam(value="id", required=false) String id,
            @RequestParam(value="page", required=false, defaultValue="0") int page,
            @RequestParam(value="size", required=false, defaultValue="2") int pagesize,
            @RequestParam(value="sort", required=false, defaultValue="") List<String> sort
    )
    {
        Page<Suscripcion> suscripciones = suscripcionService.getSuscripciones(
                null,
                PageRequest.of(
                        0, 10,
                        Sort.by(List.of(Sort.Order.asc("id")))
                )
        );

        if (suscripciones.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(suscripciones);
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