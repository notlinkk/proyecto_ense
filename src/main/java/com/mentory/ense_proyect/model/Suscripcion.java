package com.mentory.ense_proyect.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "suscripciones")
public class Suscripcion {
    @Id private String id;      // Mongo DB genera automáticamente el ID
    //private String plan;      // El tipo de plan de la suscripción se agregará posteriormente
    private String fechaInicio; // Fecha de inicio de la suscripción
    private String fechaFin;    // Fecha de fin de la suscripción
    private double precio;       // Precio de la suscripción
    private boolean activa;     // Estado de la suscripción

    private String compradorId;
    private String leccionAsociadaId;

    // Constructor
    public Suscripcion(){}
    public Suscripcion(String fechaInicio, String fechaFin, double precio, boolean activa, String comprador, String leccionAsociada) {
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.precio = precio;
        this.activa = activa;
        this.compradorId = comprador;
        this.leccionAsociadaId = leccionAsociada;
    }

    // Getters and Setters
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
