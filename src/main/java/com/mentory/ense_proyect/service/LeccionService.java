package com.mentory.ense_proyect.service;

import com.mentory.ense_proyect.exception.LeccionNotFoundException;
import com.mentory.ense_proyect.exception.DuplicatedLeccionException;
import com.mentory.ense_proyect.model.Leccion;
import com.mentory.ense_proyect.repository.LeccionRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import java.util.*;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

@Service
public class LeccionService {
    private final LeccionRepository leccionRepository;

    @Autowired
    public LeccionService(LeccionRepository leccionRepository) {
        this.leccionRepository = leccionRepository;

        leccionRepository.save(new Leccion("pepe10","Lección de prueba","Esta es una lección de prueba"));
    }

    // CRUD
    public Leccion addLeccion(Leccion leccion) throws DuplicatedLeccionException {
        if (!leccionRepository.exists(Example.of(leccion))) {
            return leccionRepository.save(leccion);
        } else {
            throw new DuplicatedLeccionException(leccion);
        }
    }

    public Set<Leccion> getLecciones(){
        return new HashSet<>(leccionRepository.findAll());
    }

    public Leccion getLeccion(String id) throws LeccionNotFoundException {
        return leccionRepository.findById(id).orElseThrow(() -> new LeccionNotFoundException(id));
    }

    // Sería un PATCH, cambia sobre el mismo objeto
    public Leccion updateLeccion(String id, Leccion leccion) throws LeccionNotFoundException {
        Leccion leccionToUpdate = leccionRepository.findById(id).orElseThrow(() -> new LeccionNotFoundException(id));

        // copiar datos de leccion a leccionToUpdate, ignorando los null
        BeanUtils.copyProperties(leccion,leccionToUpdate, getNullPropertyNames(leccion));

        return leccionRepository.save(leccionToUpdate);
    }

    // Función auxiliar para obtener los nombres de las propiedades nulas
    private String[] getNullPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        return Arrays.stream(src.getPropertyDescriptors())
                .map(java.beans.PropertyDescriptor::getName)
                .filter(name -> src.getPropertyValue(name) == null)
                .toArray(String[]::new);
    }
    
    public void deleteLeccion(String id) throws LeccionNotFoundException {
        if (leccionRepository.existsById(id)) {
            leccionRepository.deleteById(id);
        } else {
            throw new LeccionNotFoundException(id);
        }
    }
}
