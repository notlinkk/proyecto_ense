package com.mentory.ense_proyect.controller;

import com.mentory.ense_proyect.exception.DuplicatedModuloException;
import com.mentory.ense_proyect.exception.ModuloNotFoundException;
import com.mentory.ense_proyect.model.Modulo;
import com.mentory.ense_proyect.service.ModuloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.util.Set;

@RestController
@RequestMapping("modulos")
public class ModuloController {
    ModuloService moduloService;

    @Autowired
    public ModuloController(ModuloService moduloService) {
        this.moduloService = moduloService;
    }

    @GetMapping("{id}")
    public ResponseEntity<Modulo> getModulo(@PathVariable("id") String id) {
        try {
            return ResponseEntity.ok(moduloService.getModulo(id));
        } catch (ModuloNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}