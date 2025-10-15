package com.mentory.ense_proyect.exception;

public class HabilidadNotFoundException extends Throwable {
    private final String id;
    public HabilidadNotFoundException(String id) {
        super("Habilidad no encontrada con el id: " + id);
        this.id = id;
    }
    public String getId() {
        return id;
    }
}
