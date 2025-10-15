package com.mentory.ense_proyect.exception;

public class LeccionNotFoundException extends Throwable {
    private final String id;
    public LeccionNotFoundException(String id) {
        super("Leccion no encontrada con el id: " + id);
        this.id = id;
    }
    public String getId() {
        return id;
    }
}
