package com.mentory.ense_proyect.controller;

import com.mentory.ense_proyect.exception.SuscripcionNotFoundException;
import com.mentory.ense_proyect.model.Suscripcion;
import com.mentory.ense_proyect.service.SuscripcionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("suscripciones")
public class SuscripcionController {
    SuscripcionService suscripcionService;

    @Autowired
    public SuscripcionController(SuscripcionService suscripcionService) {
        this.suscripcionService = suscripcionService;
    }

    @GetMapping("{id}")
    public ResponseEntity<Suscripcion> getModulo(@PathVariable("id") String id) {
        try {
            return ResponseEntity.ok(suscripcionService.getSuscripcion(id));
        } catch (SuscripcionNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}