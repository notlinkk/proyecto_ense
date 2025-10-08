package com.mentory.ense_proyect.service;

import com.mentory.ense_proyect.model.Usuario;
import com.mentory.ense_proyect.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;

    @Autowired
    public UsuarioService(UsuarioRepository userRepository) {
        this.usuarioRepository = userRepository;

        userRepository.save(
                new Usuario("us10", "Fernando", "García", "Rodriguez", "fernandito54@gmail.com", "1234")
        );
    }
    public Usuario addUsuario(Usuario user) {
        if (!usuarioRepository.existsById(user.getId())) {
            return usuarioRepository.save(user);
        } else {
            throw new IllegalArgumentException("El usuario con ID " + user.getId() + " ya existe.");
        }
    }

    public Usuario getUser(String id){
        if (usuarioRepository.existsById(id)){
            return  usuarioRepository.findById(id).get();
        } else {
            throw new IllegalArgumentException("El usuario con ID " + id + " no existe.");
        }
    }
}
