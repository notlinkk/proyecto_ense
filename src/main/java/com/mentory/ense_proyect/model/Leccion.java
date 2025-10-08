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
    private Usuario propietario; // ID del usuario al que pertenece la lección

    private Set<Suscripcion> suscripciones = new HashSet<>(); // Suscripciones asociadas a la lección
    private HashMap<Integer,Modulo> modulos = new HashMap<>(); // Módulos que componen la lección
    private Set<Habilidad> habilidad = new HashSet<>();

    // Constructor
    public Leccion(Usuario propietario, String nombre, String descripcion) {
        this.descripcion = descripcion;
        this.propietario = propietario;
        this.nombre = nombre;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }
}
