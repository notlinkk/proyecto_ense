package com.mentory.ense_proyect.controller;

import com.mentory.ense_proyect.exception.DuplicatedUsuarioException;
import com.mentory.ense_proyect.exception.UsuarioNotFoundException;
import com.mentory.ense_proyect.model.Usuario;
import com.mentory.ense_proyect.service.UsuarioService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import com.fasterxml.jackson.annotation.JsonView;

import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.JsonPatchOperation;

import java.util.*;

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
    public ResponseEntity<Page<Usuario>> getUsers(
            @RequestParam(value="nombre", required=false) String nombre,
            @RequestParam(value="page", required=false, defaultValue="0") int page,
            @RequestParam(value="size", required=false, defaultValue="2") int pagesize,
            @RequestParam(value="sort", required=false, defaultValue="") List<String> sort
    ) {
        Page<Usuario> usuarios = usuarioService.getUsers(
                nombre,
                PageRequest.of(
                        page, pagesize,
                        Sort.by(sort.stream()
                                .map(key -> key.startsWith("-") ?
                                        Sort.Order.desc(key.substring(1)) :
                                        Sort.Order.asc(key))
                                .toList()
                        )
                )
        );

        if (usuarios.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(usuarios);
    }

    @PostMapping
    @JsonView(Usuario.CreateView.class)
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
