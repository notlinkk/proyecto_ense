package com.mentory.ense_proyect.model;

public class Suscripcion {
    private String idUsuario; // ID del usuario al que pertenece la suscripción
    private String plan; // Nombre del plan de suscripción
    private String fechaInicio; // Fecha de inicio de la suscripción
    private String fechaFin; // Fecha de fin de la suscripción
    private boolean activa; // Estado de la suscripción

    // Constructor
    public Suscripcion(String idUsuario, String plan, String fechaInicio, String fechaFin) {
        this.idUsuario = idUsuario;
        this.plan = plan;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.activa = true; // Por defecto, la suscripción está activa
    }

    // Getters and Setters
    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getPlan() {
        return plan;
    }

    public void setPlan(String plan) {
        this.plan = plan;
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
