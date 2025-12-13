package com.mentory.ense_proyect.model.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;


@Entity
@Table(name = "subscriptions")

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Subscription {

    public interface CreateView {}                          // Vista para la creación del usuario
    public interface ExternalView extends CreateView {}     // Vista externa del usuario
    public interface OwnView extends  ExternalView {} 

    @Id @JsonView(CreateView.class)
    private String id;      
    //private String plan;      Añadir en posteriores actualizaciones
    @JsonView(OwnView.class)
    private String startDate;   // Fecha de inicio de la suscripción
    
    @JsonView(OwnView.class)
    private String endDate;     // Fecha de fin de la suscripción
    
    @JsonView(OwnView.class)
    private double prize;       // Precio de la suscripción
    
    @JsonView(OwnView.class)
    private boolean active;     // Estado de la suscripción

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "username", nullable = false)
    @JsonIgnore
    private User buyer;         // Comprador de la suscripción

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id", nullable = false)
    @JsonIgnore
    private Lesson lesson;      // Lección asociada a la suscripción

    // Constructor vacio
    public Subscription(){
    }

    // Constructor
    public Subscription(String startDate, String endDate, double prize, boolean active, User buyer, Lesson lesson) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.prize = prize;
        this.active = active;
        this.buyer = buyer;
        this.lesson = lesson;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String fechaInicio) {
        this.startDate = fechaInicio;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String fechaFin) {
        this.endDate = fechaFin;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean activa) {
        this.active = activa;
    }

    public double getPrize() {
        return prize;
    }

    public void setPrize(double precio) {
        this.prize = precio;
    }

    public User getBuyer() {
        return buyer;
    }

    public void setBuyer(User comprador) {
        this.buyer = comprador;
    }

    public Lesson getLesson() {
        return lesson;
    }

    public void setLesson(Lesson leccion) {
        this.lesson = leccion;
    }
}
