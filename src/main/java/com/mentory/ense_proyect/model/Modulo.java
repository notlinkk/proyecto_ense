package com.mentory.ense_proyect.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "modulos")
public class Modulo {
    @Id private String id;
    private String titulo;      // Título del módulo
    private String descripcion; // Descripción del módulo
    private String contenido;   // Puede ser texto, URL de video, etc.
    private int duracionMins;   // Duración en minutos
    private int orden;          // Orden del módulo dentro de la lección (ver como hacerlo)

    private Leccion leccion; // Lección a la que pertenece dicho módulo

    // Constructor
    public Modulo( String titulo, String descripcion, String contenido, int duracionMins, int orden, Leccion leccion) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.contenido = contenido;
        this.duracionMins = duracionMins;
        this.orden = orden;
        this.leccion = leccion;
    }

    // Getters and Setters

}
