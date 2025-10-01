package com.mentory.ense_proyect.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Leccion {
    private Usuario propietario; // ID del usuario al que pertenece la lección
    private Set<Suscripcion> suscripciones = new HashSet<>(); // Suscripciones asociadas a la lección
    private HashMap<Integer,Modulo> modulos = new HashMap<>(); // Módulos que componen la lección
    private String idLeccion;
    private Habilidad habilidad;

    // Constructor
    public Leccion(Usuario propietario, String idLeccion) {
        this.propietario = propietario;
        this.idLeccion = idLeccion;
    }

    // Getters and Setters


}
