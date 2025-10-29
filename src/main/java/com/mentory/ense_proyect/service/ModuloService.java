package com.mentory.ense_proyect.service;

import com.mentory.ense_proyect.exception.*;
import com.mentory.ense_proyect.model.Habilidad;
import com.mentory.ense_proyect.model.Modulo;
import com.mentory.ense_proyect.repository.ModuloRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.github.fge.jsonpatch.JsonPatchOperation;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;

@Service
public class ModuloService {
    private final ModuloRepository moduloRepository;
    private final ObjectMapper mapper;

    @Autowired
    public ModuloService(ModuloRepository moduloRepository,  ObjectMapper mapper) {
        this.moduloRepository=moduloRepository;
        this.mapper=mapper;

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

    public Modulo updateModulo(String nombre, List<JsonPatchOperation> changes) throws ModuloNotFoundException, JsonPatchException {
        Modulo modulo = moduloRepository.findById(nombre).orElseThrow(() -> new ModuloNotFoundException(nombre));
        JsonPatch patch = new JsonPatch(changes);
        JsonNode patched = patch.apply(mapper.convertValue(modulo, JsonNode.class));
        Modulo updated = mapper.convertValue(patched, Modulo.class);
        return moduloRepository.save(updated);
    }

    public void deleteModulo(String id) throws ModuloNotFoundException {
        if(moduloRepository.existsById(id)){
            moduloRepository.deleteById(id);
        } else {
            throw new ModuloNotFoundException(id);
        }
    }

}
