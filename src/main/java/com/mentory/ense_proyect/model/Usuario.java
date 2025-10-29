package com.mentory.ense_proyect.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import com.fasterxml.jackson.annotation.JsonIgnore;


@Document(collection = "usuarios")
public class Usuario {
    // El username será el ID único del usuario
    @Id private String username;      // ID único del usuario
    private String nombre;      // Nombre del usuario
    private String apellido1;   // Primer apellido del usuario
    private String apellido2;   // Segundo apellido del usuario
    private String email;       // Correo electrónico del usuario
    private String password;    // Contraseña del usuario

    @JsonIgnore
    private Set<Suscripcion> suscripcionesCompradas = new HashSet<>();     // Suscripciones del usuario
    @JsonIgnore
    private HashMap<String,Leccion> leccionesCreadas = new HashMap<>();   // Lecciones creadas por el usuario

    // Constructor
    public Usuario() {
        this.suscripcionesCompradas = new HashSet<>();
        this.leccionesCreadas = new HashMap<>();
    }

    // Con ID (para obtener usuarios existentes)
    public Usuario(String username, String nombre, String apellido1, String apellido2, String email, String password) {
        this.username=username;
        this.nombre = nombre;
        this.apellido1 = apellido1;
        this.apellido2 = apellido2;
        this.email = email;
        this.password = password;
        this.suscripcionesCompradas = new HashSet<>();
        this.leccionesCreadas = new HashMap<>();
    }

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String id) {
        this.username = id;
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
