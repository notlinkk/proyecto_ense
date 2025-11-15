package com.mentory.ense_proyect.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.annotation.JsonIgnore;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "subscriptions")
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

    @JsonView(CreateView.class)
    private User buyer;         // Comprador de la suscripción

    @JsonView(OwnView.class)
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
}
