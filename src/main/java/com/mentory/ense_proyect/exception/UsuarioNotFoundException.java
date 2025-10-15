package com.mentory.ense_proyect.exception;

public class UsuarioNotFoundException extends Throwable {
    private final String id;
    public UsuarioNotFoundException(String id) {
        super("Usuario no encontrado con el id: " + id);
        this.id = id;
    }
    public String getId() {
        return id;
    }
}
