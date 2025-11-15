package com.mentory.ense_proyect.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.annotation.JsonIgnore;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "lessons")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Lesson {
    // La distincion se verá reflejada en la url del recurso /users/{userId}/lessons/{lessonId}
    // Se puede repetir nombre de la leccion, pero tendrá que tener propietarios distintos
    public interface CreateView {}
    public interface ExternalView extends CreateView{}
    public interface OwnView extends ExternalView {}

    @Id @JsonView(CreateView.class)
    private String id;

    @JsonView(CreateView.class)
    private String name;

    @JsonView(CreateView.class)
    private String description;

    @JsonView(CreateView.class)
    private String ownerId; // ID del usuario al que pertenece la lección

    @JsonView(OwnView.class)
    private Set<Subscription> subscriptions = new HashSet<>(); // Suscripciones asociadas a la lección
    
    @JsonView(ExternalView.class)
    private HashMap<Integer,Module> modules = new HashMap<>(); // Módulos que componen la lección
    
    @JsonView(ExternalView.class)
    private Set<Ability> ability = new HashSet<>();

    // Constructor
    public Lesson(){}
    public Lesson(String propietarioId, String nombre, String descripcion) {
        this.description = descripcion;
        this.ownerId = propietarioId;
        this.name = nombre;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String nombre) {
        this.name = nombre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String descripcion) {
        this.description = descripcion;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String propietarioId) {
        this.ownerId = propietarioId;
    }

    public Set<Subscription> getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(Set<Subscription> suscripciones) {
        this.subscriptions = suscripciones;
    }

    public Set<Ability> getAbility() {
        return ability;
    }

    public void setHabilidad(Set<Ability> habilidad) {
        this.ability = habilidad;
    }

    public HashMap<Integer, Module> getModules() {
        return modules;
    }

    public void setModules(HashMap<Integer, Module> modulos) {
        this.modules = modulos;
    }
}
