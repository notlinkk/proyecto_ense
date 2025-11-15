package com.mentory.ense_proyect.exception;

public class SubscriptionNotFoundException extends Throwable {
    private final String id;
    public SubscriptionNotFoundException(String id) {
        super("Suscripcion no encontrada con el id: " + id);
        this.id = id;
    }
    public String getId() {
        return id;
    }
}
