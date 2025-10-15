package com.mentory.ense_proyect.controller;

import com.mentory.ense_proyect.exception.LeccionNotFoundException;
import com.mentory.ense_proyect.service.LeccionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("lecciones")
public class LeccionController {
    LeccionService leccionService;

    @Autowired
    public LeccionController(LeccionService leccionService) {
        this.leccionService = leccionService;
    }

    @GetMapping("{id}")
    public ResponseEntity getLeccion(@PathVariable("id") String id) {
        try {
            return ResponseEntity.ok(leccionService.getLeccion(id));
        } catch (LeccionNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
