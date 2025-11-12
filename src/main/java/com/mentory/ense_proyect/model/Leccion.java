package com.mentory.ense_proyect.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.annotation.JsonIgnore;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "lecciones")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Leccion {
    // La distincion se verá reflejada en la url del recurso /users/{userId}/lessons/{lessonId}
    // Se puede repetir nombre de la leccion, pero tendrá que tener propietarios distintos
    public interface CreateView {}
    public interface ExternalView extends CreateView{}
    public interface OwnView extends ExternalView {}

    @Id @JsonView(CreateView.class)
    private String id;

    @JsonView(CreateView.class)
    private String nombre;

    @JsonView(CreateView.class)
    private String descripcion;

    @JsonView(CreateView.class)
    private String propietarioId; // ID del usuario al que pertenece la lección

    @JsonView(OwnView.class)
    private Set<Suscripcion> suscripciones = new HashSet<>(); // Suscripciones asociadas a la lección
    
    @JsonView(ExternalView.class)
    private HashMap<Integer,Modulo> modulos = new HashMap<>(); // Módulos que componen la lección
    
    @JsonView(ExternalView.class)
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
