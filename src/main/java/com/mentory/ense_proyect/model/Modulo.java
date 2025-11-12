package com.mentory.ense_proyect.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.annotation.JsonIgnore;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "modulos")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Modulo {

    public interface CreateView {}                          // Vista para la creación del usuario
    public interface ExternalView extends CreateView {}     // Vista externa del usuario
    public interface OwnView extends  ExternalView {} 

    @Id @JsonView(CreateView.class)
    private String id;

    @JsonView(CreateView.class)
    private String titulo;      // Título del módulo

    @JsonView(CreateView.class)
    private String descripcion; // Descripción del módulo

    @JsonView(CreateView.class)
    private String contenido;   // Puede ser texto, URL de video, etc.

    @JsonView(CreateView.class)
    private int duracionMins;   // Duración en minutos

    @JsonView(ExternalView.class)
    private int orden;          // Orden del módulo dentro de la lección (ver como hacerlo)

    @JsonView(OwnView.class)
    private String leccionId; // Lección a la que pertenece dicho módulo

    // Constructor
    public Modulo(){}

    public Modulo( String titulo, String descripcion, String contenido, int duracionMins, int orden, String leccionId) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.contenido = contenido;
        this.duracionMins = duracionMins;
        this.orden = orden;
        this.leccionId = leccionId;
    }

    // Getters and Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public int getDuracionMins() {
        return duracionMins;
    }

    public void setDuracionMins(int duracionMins) {
        this.duracionMins = duracionMins;
    }

    public int getOrden() {
        return orden;
    }

    public void setOrden(int orden) {
        this.orden = orden;
    }

    public String getLeccionId() {
        return leccionId;
    }

    public void setLeccionId(String leccionId) {
        this.leccionId = leccionId;
    }
}
