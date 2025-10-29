package com.mentory.ense_proyect.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.JsonPatchOperation;
import com.mentory.ense_proyect.exception.UsuarioNotFoundException;
import com.mentory.ense_proyect.exception.DuplicatedUsuarioException;
import com.mentory.ense_proyect.model.Usuario;
import com.mentory.ense_proyect.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final ObjectMapper mapper;

    @Autowired
    public UsuarioService(UsuarioRepository userRepository, ObjectMapper mapper) {
        this.usuarioRepository = userRepository;
        this.mapper = new ObjectMapper();

        userRepository.save(
                new Usuario("fernandaco2011", "Fernando", "García", "Rodriguez", "fernandito54@gmail.com", "1234")
        );
    }

    // CRUD
    public Usuario addUser(Usuario user) throws DuplicatedUsuarioException {
        if (!usuarioRepository.exists(Example.of(user))) {
            return usuarioRepository.save(user);
        } else {
            throw new DuplicatedUsuarioException(user);
        }
    }

    public Usuario updateUser(String username, List<JsonPatchOperation> changes) throws UsuarioNotFoundException, JsonPatchException {
        Usuario usuario = usuarioRepository.findById(username).orElseThrow(() -> new UsuarioNotFoundException(username));
        JsonPatch patch = new JsonPatch(changes);
        JsonNode patched = patch.apply(mapper.convertValue(usuario, JsonNode.class));
        Usuario updated = mapper.convertValue(patched, Usuario.class);
        return usuarioRepository.save(updated);
    }

    public Set<Usuario> getUsers(){
        return new HashSet<>(usuarioRepository.findAll());
    }

    public Usuario getUser(String id) throws UsuarioNotFoundException {
        return usuarioRepository.findById(id).orElseThrow(() -> new UsuarioNotFoundException(id));
    }



    public void deleteUsuario(String id) throws UsuarioNotFoundException {
        if(usuarioRepository.existsById(id)){
            usuarioRepository.deleteById(id);
        } else {
            throw new UsuarioNotFoundException(id);
        }
    }


}
