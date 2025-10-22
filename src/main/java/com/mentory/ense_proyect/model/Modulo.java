package com.mentory.ense_proyect.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "modulos")
public class Modulo {
    // id de mongo, se  pueden repetir titulos dentro de lecciones distintas.
    @Id private String id;
    private String titulo;      // Título del módulo
    private String descripcion; // Descripción del módulo
    private String contenido;   // Puede ser texto, URL de video, etc.
    private int duracionMins;   // Duración en minutos
    private int orden;          // Orden del módulo dentro de la lección (ver como hacerlo)

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
