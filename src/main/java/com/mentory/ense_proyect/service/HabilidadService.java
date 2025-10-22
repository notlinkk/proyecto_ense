package com.mentory.ense_proyect.service;

import com.mentory.ense_proyect.model.Habilidad;
import com.mentory.ense_proyect.repository.HabilidadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;


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
                    new Habilidad("comunicacion-efectiva", "Capacidad para expresar ideas con claridad y escuchar activamente."),
                    new Habilidad( "trabajo-en-equipo", "Habilidad para colaborar con otros hacia un objetivo común."),
                    new Habilidad( "liderazgo", "Capacidad para guiar, motivar y coordinar a un grupo."),
                    new Habilidad( "creatividad", "Generación de ideas nuevas o enfoques originales para resolver problemas."),
                    new Habilidad( "autodisciplina", "Mantener constancia y compromiso con los objetivos.")
            ));
        }
    }

    // CRUD
    public Habilidad createHabilidad(Habilidad habilidad) throws DuplicatedHabilidadException {
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
    public Habilidad updateHabilidade(String nombre, Habilidad habilidad) throws HabilidadNotFoundException {
        if (habilidadRepository.existsById(nombre)) {
            Habilidad habilidadToUpdate = habilidadRepository.findById(nombre).orElseThrow();
            Habilidad updatedHabilidad = new Habilidad(
                    habilidadToUpdate.nombre(),
                    habilidad.descripcion() != null ? habilidad.descripcion() : habilidadToUpdate.descripcion()
            );
            return habilidadRepository.save(updatedHabilidad);
        } else {
            throw new HabilidadNotFoundException(nombre);

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
