package com.mentory.ense_proyect.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Usuario {
    private Set<Suscripcion> suscripciones = new HashSet<>();
    private HashMap<Integer,Leccion> lecciones = new HashMap<>();
    private String id;
    private String nombre;
    private String email;
    private String password;

    // Constructor
    public Usuario(String id, String nombre, String email, String password) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.password = password;
    }
}
