package com.mentory.ense_proyect.exception;

public class ModuleNotFoundException extends Throwable {
    private final String id;
    public ModuleNotFoundException(String id) {
        super("Modulo no encontrado con el id: " + id);
        this.id = id;
    }
    public String getId() {
        return id;
    }
}
