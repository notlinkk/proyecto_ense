package com.mentory.ense_proyect.exception;

public class SuscripcionNotFoundException extends Throwable {
    private final String id;
    public SuscripcionNotFoundException(String id) {
        super("Suscripcion no encontrada con el id: " + id);
        this.id = id;
    }
    public String getId() {
        return id;
    }
}
