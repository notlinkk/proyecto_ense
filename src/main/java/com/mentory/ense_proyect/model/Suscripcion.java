package com.mentory.ense_proyect.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.annotation.JsonIgnore;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "suscripciones")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Suscripcion {

    public interface CreateView {}                          // Vista para la creación del usuario
    public interface ExternalView extends CreateView {}     // Vista externa del usuario
    public interface OwnView extends  ExternalView {} 

    @Id @JsonView(CreateView.class)
    private String id;      
    //private String plan;      Añadir en posteriores actualizaciones
    @JsonView(OwnView.class)
    private String fechaInicio; // Fecha de inicio de la suscripción
    
    @JsonView(OwnView.class)
    private String fechaFin;    // Fecha de fin de la suscripción
    
    @JsonView(OwnView.class)
    private double precio;       // Precio de la suscripción
    
    @JsonView(OwnView.class)
    private boolean activa;     // Estado de la suscripción

    @JsonView(CreateView.class)
    private Usuario comprador;

    @JsonView(OwnView.class)
    private Leccion leccionAsociada;

    // Constructor vacio
    public Suscripcion(){
    }

    // Constructor
    public Suscripcion(String fechaInicio, String fechaFin, double precio, boolean activa, Usuario comprador, Leccion leccionAsociada) {
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.precio = precio;
        this.activa = activa;
        this.comprador = comprador;
        this.leccionAsociada = leccionAsociada;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }
    public String getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(String fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public String getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(String fechaFin) {
        this.fechaFin = fechaFin;
    }

    public boolean isActiva() {
        return activa;
    }

    public void setActiva(boolean activa) {
        this.activa = activa;
    }
}
