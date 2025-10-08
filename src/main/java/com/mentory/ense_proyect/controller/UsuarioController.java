package com.mentory.ense_proyect.controller;

import com.mentory.ense_proyect.model.Usuario;
import com.mentory.ense_proyect.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("users")
public class UsuarioController {
    UsuarioService usuarioService;

    @Autowired
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("{id}")
    public ResponseEntity <Usuario> getUsuario(@PathVariable("id") String id){
        try{
            return ResponseEntity.ok(usuarioService.getUser(id));
        } catch (IllegalArgumentException e){
            return ResponseEntity.notFound().build();
        }
    }
}
