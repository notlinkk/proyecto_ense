package com.mentory.ense_proyect.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "usuarios")
public class Usuario {
    @Id private String id;      // ID único del usuario
    private String nombre;      // Nombre del usuario
    private String apellido1;   // Primer apellido del usuario
    private String apellido2;   // Segundo apellido del usuario
    private String email;       // Correo electrónico del usuario
    private String password;    // Contraseña del usuario

    private Set<Suscripcion> suscripcionesCompradas = new HashSet<>();     // Suscripciones del usuario
    private HashMap<String,Leccion> leccionesCreadas = new HashMap<>();   // Lecciones creadas por el usuario

    // Constructor
    // Sin ID (para creación de nuevos usuarios)
    public Usuario(String nombre, String apellido1, String apellido2, String email, String password) {
        this.nombre = nombre;
        this.apellido1 = apellido1;
        this.apellido2 = apellido2;
        this.email = email;
        this.password = password;
    }

    // Con ID (para obtener usuarios existentes)
    public Usuario(String id, String nombre, String apellido1, String apellido2, String email, String password) {
        this.id=id;
        this.nombre = nombre;
        this.apellido1 = apellido1;
        this.apellido2 = apellido2;
        this.email = email;
        this.password = password;
    }
    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getApellido1() {
        return apellido1;
    }

    public void setApellido1(String apellido1) {
        this.apellido1 = apellido1;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido2() {
        return apellido2;
    }

    public void setApellido2(String apellido2) {
        this.apellido2 = apellido2;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Suscripcion> getSuscripcionesCompradas() {
        return suscripcionesCompradas;
    }

    public void setSuscripcionesCompradas(Set<Suscripcion> suscripcionesCompradas) {
        this.suscripcionesCompradas = suscripcionesCompradas;
    }

    public HashMap<String, Leccion> getLeccionesCreadas() {
        return leccionesCreadas;
    }

    public void setLeccionesCreadas(HashMap<String, Leccion> leccionesCreadas) {
        this.leccionesCreadas = leccionesCreadas;
    }


}
