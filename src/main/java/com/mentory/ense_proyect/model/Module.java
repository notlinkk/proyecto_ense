package com.mentory.ense_proyect.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.annotation.JsonIgnore;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "modules")
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
    private String content;   // Puede ser texto, URL de video, etc.

    @JsonView(CreateView.class)
    private int duration;   // Duración en minutos

    @JsonView(ExternalView.class)
    private int order;          // Orden del módulo dentro de la lección (ver como hacerlo)

    @JsonView(OwnView.class)
    private String lessonId; // Lección a la que pertenece dicho módulo

    // Constructor
    public Module(){}

    public Module( String titulo, String descripcion, String contenido, int duracionMins, int orden, String leccionId) {
        this.title = titulo;
        this.description = descripcion;
        this.content = contenido;
        this.duration = duracionMins;
        this.order = orden;
        this.lessonId = leccionId;
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

    public int getOrder() {
        return order;
    }

    public void setOrder(int orden) {
        this.order = orden;
    }

    public String getLessonId() {
        return lessonId;
    }

    public void setLessonId(String leccionId) {
        this.lessonId = leccionId;
    }
}
