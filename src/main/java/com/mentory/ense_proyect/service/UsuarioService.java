package com.mentory.ense_proyect.service;

import com.mentory.ense_proyect.exception.UsuarioNotFoundException;
import com.mentory.ense_proyect.exception.DuplicatedUsuarioException;
import com.mentory.ense_proyect.model.Usuario;
import com.mentory.ense_proyect.repository.UsuarioRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import java.util.*;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

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

    // CRUD
    public Usuario addUser(Usuario user) throws DuplicatedUsuarioException {
        if (!usuarioRepository.exists(Example.of(user))) {
            return usuarioRepository.save(user);
        } else {
            throw new DuplicatedUsuarioException(user);
        }
    }

    public Set<Usuario> getUsers(){
        return new HashSet<>(usuarioRepository.findAll());
    }

    public Usuario getUser(String id) throws UsuarioNotFoundException {
        return usuarioRepository.findById(id).orElseThrow(() -> new UsuarioNotFoundException(id));
    }

    public Usuario updateUsuario(String id, Usuario user) throws UsuarioNotFoundException {
        Usuario userToUpdate = usuarioRepository.findById(id).orElseThrow(() -> new UsuarioNotFoundException(id));

        // copiar datos de user a userToUpdate, ignorando los null
        BeanUtils.copyProperties(user,userToUpdate, getNullPropertyNames(user));

        return usuarioRepository.save(userToUpdate);
    }

    private String[] getNullPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        return Arrays.stream(src.getPropertyDescriptors())
                .map(java.beans.PropertyDescriptor::getName)
                .filter(name -> src.getPropertyValue(name) == null)
                .toArray(String[]::new);
    }

    public void deleteUsuario(String id) throws UsuarioNotFoundException {
        if(usuarioRepository.existsById(id)){
            usuarioRepository.deleteById(id);
        } else {
            throw new UsuarioNotFoundException(id);
        }
    }


}
