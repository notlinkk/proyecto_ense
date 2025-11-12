package com.mentory.ense_proyect.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.annotation.JsonIgnore;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Document(collection = "usuarios")
@JsonInclude(JsonInclude.Include.NON_NULL)

public class Usuario {
    public interface CreateView {}                          // Vista para la creación del usuario
    public interface ExternalView extends CreateView {}     // Vista externa del usuario
    public interface OwnView extends  ExternalView {}         // Vista propia del usuario

    @Id @JsonView(CreateView.class)
    private String username;      // ID único del usuario, empleado como nickname del usuario en el servicio

    @JsonView(CreateView.class)
    private String nombre;      // Nombre del usuario

    @JsonView(CreateView.class)
    private String apellido1;   // Primer apellido del usuario

    @JsonView(CreateView.class)
    private String apellido2;   // Segundo apellido del usuario

    @JsonView(CreateView.class)
    private String email;       // Correo electrónico del usuario

    @JsonIgnore                 // No se serializa la contraseña al convertir a JSON
    private String password;    // Contraseña del usuario

    @JsonView(OwnView.class)
    private Set<Suscripcion> suscripcionesCompradas = new HashSet<>();     // Suscripciones del usuario

    @JsonView(ExternalView.class)
    private HashMap<String,Leccion> leccionesCreadas = new HashMap<>();   // Lecciones creadas por el usuario

    // Constructor
    public Usuario() {
    }

    // Con ID (para obtener usuarios existentes)
    public Usuario(String username, String nombre, String apellido1, String apellido2, String email, String password) {
        this.username=username;
        this.nombre = nombre;
        this.apellido1 = apellido1;
        this.apellido2 = apellido2;
        this.email = email;
        this.password = password;
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
