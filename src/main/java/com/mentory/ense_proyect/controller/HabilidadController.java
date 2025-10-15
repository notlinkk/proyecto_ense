package com.mentory.ense_proyect.controller;

import com.mentory.ense_proyect.exception.HabilidadNotFoundException;
import com.mentory.ense_proyect.model.Habilidad;
import com.mentory.ense_proyect.service.HabilidadService;
import com.mentory.ense_proyect.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("habilidades")
public class HabilidadController {
    HabilidadService habilidadService;

    @Autowired
    public HabilidadController(HabilidadService habilidadService) {
        this.habilidadService = habilidadService;
    }

    @GetMapping("{id}")
    public ResponseEntity <Habilidad> getHabilidad(@PathVariable("id") String id) {
        try {
            return ResponseEntity.ok(habilidadService.getHabilidad(id));
        } catch (HabilidadNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<Set<Habilidad>> getHabilidades() {
        Set<Habilidad> habilidades = habilidadService.getHabilidades();
        if (habilidades.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 si no hay resultados
        }
        return ResponseEntity.ok(habilidades); // 200 + JSON con las habilidades
    }

}


