package com.mentory.ense_proyect.model;

public class Modulo {
    private String id;
    private String contenido; // Puede ser texto, URL de video, etc.
    private int duracionMins; // Duración en minutos
    private int orden; // Orden del módulo dentro de la lección (ver como hacerlo)

    // Constructor
    public Modulo(String id, String contenido, int duracionMins, int orden) {
        this.id = id;
        this.contenido = contenido;
        this.duracionMins = duracionMins;
        this.orden = orden;
    }

    // Getters and Setters

}
