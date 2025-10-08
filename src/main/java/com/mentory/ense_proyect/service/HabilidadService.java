package com.mentory.ense_proyect.service;

import com.mentory.ense_proyect.model.Habilidad;
import com.mentory.ense_proyect.repository.HabilidadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class HabilidadService {
    private final HabilidadRepository habilidadRepository;

    @Autowired
    public HabilidadService(HabilidadRepository habilidadRepository) {
        this.habilidadRepository = habilidadRepository;

        habilidadRepository.save(
            new Habilidad("Comunicación efectiva","Capacidad para expresar ideas con claridad y escuchar activamente.")
        );

        habilidadRepository.save(
                new Habilidad("Trabajo en equipo","Habilidad para colaborar con otros hacia un objetivo común.")
        );

        habilidadRepository.save(
                new Habilidad("Liderazgo","Capacidad para guiar, motivar y coordinar a un grupo.")
        );

        habilidadRepository.save(
                new Habilidad("Creatividad","Generación de ideas nuevas o enfoques originales para resolver problemas.")
        );

        habilidadRepository.save(
                new Habilidad("Autodisciplina","Mantener constancia y compromiso con los objetivos.")
        );
    }

    /*public Habilidad addHabilidad(Habilidad habilidad) throws  DuplicatedHabilidadException{
        if (!habilidadRepository.exists(Example.of(habilidad))) {
            return habilidadRepository.save(habilidad);
        } else {
            throw new IllegalArgumentException("La habilidad " + habilidad.nombre() + " ya existe.");
        }
    }*/

    public Set<Habilidad> getHabilidades(){
        return new HashSet<>(habilidadRepository.findAll());
    }

    public Habilidad getHabilidad(String id){
        if (habilidadRepository.existsById(id)){
            return habilidadRepository.findById(id).orElseThrow();
        } else {
            throw new IllegalArgumentException("La habilidad con ID " + id + " no existe.");
        }
    }
}
