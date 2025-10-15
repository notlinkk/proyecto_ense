package com.mentory.ense_proyect.service;

import com.mentory.ense_proyect.model.Habilidad;
import com.mentory.ense_proyect.repository.HabilidadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;


import java.util.*;

import com.mentory.ense_proyect.exception.*;

@Service
public class HabilidadService {
    private final HabilidadRepository habilidadRepository;

    @Autowired
    public HabilidadService(HabilidadRepository habilidadRepository) {
        this.habilidadRepository = habilidadRepository;
        if (habilidadRepository.count() == 0) {
            habilidadRepository.saveAll(List.of(
                    new Habilidad("Comunicación efectiva", "Capacidad para expresar ideas con claridad y escuchar activamente."),
                    new Habilidad( "Trabajo en equipo", "Habilidad para colaborar con otros hacia un objetivo común."),
                    new Habilidad( "Liderazgo", "Capacidad para guiar, motivar y coordinar a un grupo."),
                    new Habilidad( "Creatividad", "Generación de ideas nuevas o enfoques originales para resolver problemas."),
                    new Habilidad( "Autodisciplina", "Mantener constancia y compromiso con los objetivos.")
            ));
        }
    }

    // CRUD
    public Habilidad addHabilidad(Habilidad habilidad) throws DuplicatedHabilidadException {
        if (!habilidadRepository.exists(Example.of(habilidad))) {
            return habilidadRepository.save(habilidad);
        } else {
            throw new DuplicatedHabilidadException(habilidad);
        }
    }

    public Set<Habilidad> getHabilidades() {
        return new HashSet<>(habilidadRepository.findAll());
    }

    public Habilidad getHabilidad(String id) throws HabilidadNotFoundException {
        return habilidadRepository.findById(id).orElseThrow(() -> new HabilidadNotFoundException(id));
    }

    // Se podría usar PATCH ya que Record permite valores null. Revisar para el resto de clases
    public Habilidad updateHabilidade(String id, Habilidad habilidad) throws HabilidadNotFoundException {
        if (habilidadRepository.existsById(id)) {
            Habilidad habilidadToUpdate = habilidadRepository.findById(id).orElseThrow();
            Habilidad updatedHabilidad = new Habilidad(
                    habilidadToUpdate.id(),
                    habilidad.nombre() != null ? habilidad.nombre() : habilidadToUpdate.nombre(),
                    habilidad.descripcion() != null ? habilidad.descripcion() : habilidadToUpdate.descripcion()
            );
            return habilidadRepository.save(updatedHabilidad);
        } else {
            throw new HabilidadNotFoundException(id);

        }
    }

    public void deleteHabilidad(String id) throws HabilidadNotFoundException {
        if (habilidadRepository.existsById(id)){
            habilidadRepository.deleteById(id);
        } else {
            throw new HabilidadNotFoundException(id);
        }

    }
}
