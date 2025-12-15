package com.mentory.ense_proyect.model.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.jspecify.annotations.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


@Entity
@Table(name = "users")

@JsonInclude(JsonInclude.Include.NON_NULL)

public class User implements UserDetails{
    public interface CreateView {}                          // Vista para la creación del usuario
    public interface ExternalView extends CreateView {}     // Vista externa del usuario
    public interface OwnView extends  ExternalView {}         // Vista propia del usuario

    @Id @JsonView(CreateView.class)
    private String username;      // ID único del usuario, empleado como nickname del usuario en el servicio

    @JsonView(CreateView.class)
    private String name;      // Nombre del usuario

    @JsonView(CreateView.class)
    private String surname1;   // Primer apellido del usuario

    @JsonView(CreateView.class)
    private String surname2;   // Segundo apellido del usuario

    @JsonView(CreateView.class)
    private String email;       // Correo electrónico del usuario

    @JsonIgnore                 // No se serializa la contraseña al convertir a JSON
    private String password;    // Contraseña del usuario

    @OneToMany(mappedBy = "buyer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Subscription> subscriptions = new HashSet<>();     // Suscripciones del usuario

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_lessons",
        joinColumns = @JoinColumn(name = "username"),
        inverseJoinColumns = @JoinColumn(name = "lesson_id"))
    @JsonIgnore
    private Set<Lesson> lessons = new HashSet<>();   // Lecciones creadas por el usuario

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles",
        joinColumns = @JoinColumn(name = "username"),
        inverseJoinColumns = @JoinColumn(name = "rolename"))
    private Set<Role> roles = new HashSet<>();   // Roles del usuario
    // Constructor
    public User() {
    }

    // Con ID (para obtener usuarios existentes)
    public User(String username, String name, String surname1, String surname2, String email, String password) {
        this.username=username;
        this.name = name;
        this.surname1 = surname1;
        this.surname2 = surname2;
        this.email = email;
        this.password = password;
    }

    @Override
    @NonNull
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        
        // Add roles (e.g., ROLE_ADMIN, ROLE_TEACHER, ROLE_USER)
        for (Role role : roles) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getRolename()));
            
            // Add permissions from each role (e.g., lessons:read, lessons:write)
            for (Permission permission : role.getPermissions()) {
                authorities.add(new SimpleGrantedAuthority(permission.getId()));
            }
        }
        
        return authorities;
    }


    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String id) {
        this.username = id;
    }

    public String getSurname1() {
        return surname1;
    }

    public void setSurname1(String apellido1) {
        this.surname1 = apellido1;
    }

    public String getName() {
        return name;
    }

    public void setName(String nombre) {
        this.name = nombre;
    }

    public String getSurname2() {
        return surname2;
    }

    public void setSurname2(String apellido2) {
        this.surname2 = apellido2;
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

    public Set<Subscription> getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(Set<Subscription> suscripcionesCompradas) {
        this.subscriptions = suscripcionesCompradas;
    }
    public Set<Lesson> getLessons() {
        return lessons;
    }
    public void setLessons(Set<Lesson> leccionesCreadas) {
        this.lessons = leccionesCreadas;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
}
