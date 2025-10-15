package com.mentory.ense_proyect.controller;

import com.mentory.ense_proyect.exception.UsuarioNotFoundException;
import com.mentory.ense_proyect.model.Usuario;
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

import java.util.Set;

@RestController
@RequestMapping("users")
public class UsuarioController {
    UsuarioService usuarioService;

    @Autowired
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /*
        -Operaciones a implementar:
            -GET /users: Obtener todos los usuarios.
            -GET /users/{id}: Obtener un usuario por su ID.
            -POST /users: Crear un nuevo usuario.
            -PUT /users/{id}: Actualizar un usuario existente.
            -DELETE /users/{id}: Eliminar un usuario por su ID.

         Para los parámetros habría que replantearse  cambiar los id que crea monogo y poner como @Id otro elemento único
         Es decir, si un usuario queire buscar a otro no tiene sentido que busque por el id de mongo sino por un username único.
         Luego para getUsuarios meteer un RequestParam de username.
     */
    @GetMapping()
    public ResponseEntity <Set<Usuario>> getUsuarios(){
        return ResponseEntity.ok(usuarioService.getUsers());
    }

    @GetMapping("{id}")
    public ResponseEntity <Usuario> getUsuario(@PathVariable("id") String id){
        try{
            return ResponseEntity.ok(usuarioService.getUser(id));
        } catch (UsuarioNotFoundException e){
            return ResponseEntity.notFound().build();
        }
    }
}
