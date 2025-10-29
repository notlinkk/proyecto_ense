package com.mentory.ense_proyect.controller;

import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.JsonPatchOperation;
import com.mentory.ense_proyect.exception.DuplicatedHabilidadException;
import com.mentory.ense_proyect.exception.DuplicatedUsuarioException;
import com.mentory.ense_proyect.exception.HabilidadNotFoundException;
import com.mentory.ense_proyect.exception.UsuarioNotFoundException;
import com.mentory.ense_proyect.model.Habilidad;
import com.mentory.ense_proyect.model.Usuario;
import com.mentory.ense_proyect.service.HabilidadService;
import com.mentory.ense_proyect.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("users")
public class UsuarioController {
    UsuarioService usuarioService;

    @Autowired
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("{id}")
    public ResponseEntity <Usuario> getUser(@PathVariable("id") String id) throws UsuarioNotFoundException {
        return ResponseEntity.ok(usuarioService.getUser(id));
    }

    @GetMapping
    public ResponseEntity<Set<Usuario>> getUsers() {
        Set<Usuario> users = usuarioService.getUsers();
        if (users.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(users);
    }

    @PostMapping
    public ResponseEntity<Usuario> createUser(@RequestBody Usuario usuario) throws DuplicatedUsuarioException {
        Usuario nuevoUsuario = usuarioService.addUser(usuario);
        return ResponseEntity
                .created(MvcUriComponentsBuilder
                        .fromMethodName(UsuarioController.class, "getUser", usuario.getUsername())
                        .build()
                        .toUri())
                .body(nuevoUsuario);
    }

    @DeleteMapping({"{id}"})
    public ResponseEntity<Void> deleteUsuario(@PathVariable("id") String id) throws UsuarioNotFoundException {
        usuarioService.deleteUsuario(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("{id}")
    public ResponseEntity<Usuario> updateUser(
            @PathVariable("id") String id,
            @RequestBody List<JsonPatchOperation> changes
    ) throws UsuarioNotFoundException, JsonPatchException {
        return ResponseEntity.ok(usuarioService.updateUser(id, changes));
    }
}
