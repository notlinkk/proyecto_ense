package com.mentory.ense_proyect.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.ManyToAny;
import org.springframework.boot.webmvc.autoconfigure.WebMvcProperties.Apiversion.Use;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;



@Entity
@Table(name = "lessons")

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

    @OneToMany(mappedBy = "lesson", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonView(OwnView.class)
    private Set<Subscription> subscriptions = new HashSet<>(); // Suscripciones asociadas a la lección
    
    @OneToMany(mappedBy = "lesson", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonView(ExternalView.class)
    private Set<Module> modules = new HashSet<>(); // Módulos que componen la lección
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable (
        name = "lesson_ability",
        joinColumns = @JoinColumn(name = "lesson_id"),
        inverseJoinColumns = @JoinColumn(name = "ability_name")
    )
    @JsonView(ExternalView.class)
    private Set<Ability> abilities = new HashSet<>();

    @ManyToMany(mappedBy = "lessons")
    private Set<User> users = new HashSet<>(); // Usuarios que han creado la lección

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

    public Set<Ability> getAbilities() {
        return abilities;
    }

    public void setHabilidad(Set<Ability> habilidad) {
        this.abilities = habilidad;
    }
    public Set<Module> getModules() {
        return modules;
    }
    public void setModules(Set<Module> modulos) {
        this.modules = modulos;
    }
}
