package com.mentory.ense_proyect.service;

import com.mentory.ense_proyect.exception.SuscripcionNotFoundException;
import com.mentory.ense_proyect.exception.DuplicatedSuscripcionException;
import com.mentory.ense_proyect.model.Suscripcion;
import com.mentory.ense_proyect.repository.SuscripcionRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import java.util.*;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

@Service
public class SuscripcionService {
    private final SuscripcionRepository suscripcionRepository;

    @Autowired
    public SuscripcionService(SuscripcionRepository suscripcionRepository) {
        this.suscripcionRepository = suscripcionRepository;

        suscripcionRepository.save(new Suscripcion("15/10/2023","15/10/2024", 20.0, true,"us10" ,"l1"));
    }

    // CRUD
    public Suscripcion addSuscripcion(Suscripcion suscripcion) throws DuplicatedSuscripcionException {
        if (!suscripcionRepository.exists(Example.of(suscripcion))) {
            return suscripcionRepository.save(suscripcion);
        } else {
            throw new DuplicatedSuscripcionException(suscripcion);
        }
    }

    public Set<Suscripcion> getSuscripciones(){
        return new HashSet<>(suscripcionRepository.findAll());
    }

    public Suscripcion getSuscripcion(String id) throws SuscripcionNotFoundException {
        return suscripcionRepository.findById(id).orElseThrow(() -> new SuscripcionNotFoundException(id));
    }

    public Suscripcion updateSuscripcion(String id, Suscripcion suscripcion) throws SuscripcionNotFoundException {
        Suscripcion suscripcionToUpdate = suscripcionRepository.findById(id).orElseThrow(() -> new SuscripcionNotFoundException(id));

        // copiar datos de suscripcion a suscripcionToUpdate, ignorando los null
        BeanUtils.copyProperties(suscripcion,suscripcionToUpdate, getNullPropertyNames(suscripcion));

        return suscripcionRepository.save(suscripcionToUpdate);
    }

    private String[] getNullPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        return Arrays.stream(src.getPropertyDescriptors())
                .map(java.beans.PropertyDescriptor::getName)
                .filter(name -> src.getPropertyValue(name) == null)
                .toArray(String[]::new);
    }

    public void deleteSuscripcion(String id) throws SuscripcionNotFoundException {
        if (suscripcionRepository.existsById(id)) {
            suscripcionRepository.deleteById(id);
        } else {
            throw new SuscripcionNotFoundException(id);
        }
    }
}
