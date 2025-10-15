package com.mentory.ense_proyect.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "lecciones")
public class Leccion {
    @Id private String id;
    private String nombre;
    private String descripcion;
    private String propietarioId; // ID del usuario al que pertenece la lección

    private Set<Suscripcion> suscripciones = new HashSet<>(); // Suscripciones asociadas a la lección
    private HashMap<Integer,Modulo> modulos = new HashMap<>(); // Módulos que componen la lección
    private Set<Habilidad> habilidad = new HashSet<>();

    // Constructor
    public Leccion(){}
    public Leccion(String propietarioId, String nombre, String descripcion) {
        this.descripcion = descripcion;
        this.propietarioId = propietarioId;
        this.nombre = nombre;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getPropietarioId() {
        return propietarioId;
    }

    public void setPropietarioId(String propietarioId) {
        this.propietarioId = propietarioId;
    }

    public Set<Suscripcion> getSuscripciones() {
        return suscripciones;
    }

    public void setSuscripciones(Set<Suscripcion> suscripciones) {
        this.suscripciones = suscripciones;
    }

    public Set<Habilidad> getHabilidad() {
        return habilidad;
    }

    public void setHabilidad(Set<Habilidad> habilidad) {
        this.habilidad = habilidad;
    }

    public HashMap<Integer, Modulo> getModulos() {
        return modulos;
    }

    public void setModulos(HashMap<Integer, Modulo> modulos) {
        this.modulos = modulos;
    }
}
