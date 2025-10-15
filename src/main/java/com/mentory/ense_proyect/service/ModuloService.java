package com.mentory.ense_proyect.service;

import com.mentory.ense_proyect.exception.ModuloNotFoundException;
import com.mentory.ense_proyect.exception.DuplicatedModuloException;
import com.mentory.ense_proyect.model.Modulo;
import com.mentory.ense_proyect.repository.ModuloRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import java.util.*;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

@Service
public class ModuloService {
    private final ModuloRepository moduloRepository;

    @Autowired
    public ModuloService(ModuloRepository moduloRepository) {
        this.moduloRepository=moduloRepository;

        moduloRepository.save(new Modulo("Álgebra","Matrices y nueritos", "Contenido a mostrar",20,1,"us10" ));
    }

    // CRUD
    public Modulo addModulo(Modulo modulo) throws DuplicatedModuloException {
        if (!moduloRepository.exists(Example.of(modulo))) {
            return moduloRepository.save(modulo);
        } else {
            throw new DuplicatedModuloException(modulo);
        }
    }

    public Set<Modulo> getModulos() {
        return new HashSet<>(moduloRepository.findAll());
    }

    public Modulo getModulo(String id) throws ModuloNotFoundException {
        return moduloRepository.findById(id).orElseThrow(() -> new ModuloNotFoundException(id));
    }

    public Modulo updateModulo(String id, Modulo modulo) throws ModuloNotFoundException {
        Modulo moduloToUpdate = moduloRepository.findById(id).orElseThrow(() -> new ModuloNotFoundException(id));

        // copiar datos de modulo a moduloToUpdate, ignorando los null
        BeanUtils.copyProperties(modulo,moduloToUpdate, getNullPropertyNames(modulo));

        return moduloRepository.save(moduloToUpdate);
    }

    // Función auxiliar para obtener los nombres de las propiedades nulas
    private String[] getNullPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        return Arrays.stream(src.getPropertyDescriptors())
                .map(java.beans.PropertyDescriptor::getName)
                .filter(name -> src.getPropertyValue(name) == null)
                .toArray(String[]::new);
    }

    public void deleteModulo(String id) throws ModuloNotFoundException {
        if(moduloRepository.existsById(id)){
            moduloRepository.deleteById(id);
        } else {
            throw new ModuloNotFoundException(id);
        }
    }






}
