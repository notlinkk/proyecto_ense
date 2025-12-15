package com.mentory.ense_proyect.model.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;


@Entity
@Table(name = "modules")

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Module {

    public interface CreateView {}                          // Vista para la creación del usuario
    public interface ExternalView extends CreateView {}     // Vista externa del usuario
    public interface OwnView extends  ExternalView {} 

    @Id @JsonView(CreateView.class)
    private String id;

    @JsonView(CreateView.class)
    private String title;      // Título del módulo

    @JsonView(CreateView.class)
    private String description; // Descripción del módulo

    @JsonView(CreateView.class)
    @Column(columnDefinition = "TEXT")
    private String content;   // Puede ser texto, URL de video, etc.

    @JsonView(CreateView.class)
    private int duration;   // Duración en minutos

    @JsonView(ExternalView.class)
    private int position;          // Orden del módulo dentro de la lección (ver como hacerlo)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id", nullable = false)
    @JsonIgnore
    private Lesson lesson; // Lección a la que pertenece dicho módulo

    // Constructor
    public Module(){}

    public Module( String titulo, String descripcion, String contenido, int duracionMins, int orden) {
        this.title = titulo;
        this.description = descripcion;
        this.content = contenido;
        this.duration = duracionMins;
        this.position = orden;
    }

    // Getters and Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String titulo) {
        this.title = titulo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String descripcion) {
        this.description = descripcion;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String contenido) {
        this.content = contenido;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duracionMins) {
        this.duration = duracionMins;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int orden) {
        this.position = orden;
    }
    public Lesson getLesson() {
        return lesson;
    }
    public void setLesson(Lesson leccion) {
        this.lesson = leccion;
    }
}
