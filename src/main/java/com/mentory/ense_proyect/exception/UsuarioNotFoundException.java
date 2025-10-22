package com.mentory.ense_proyect.exception;

public class UsuarioNotFoundException extends Throwable {
    private final String username;
    public UsuarioNotFoundException(String username) {
        super("Usuario no encontrado con el id: " + username);
        this.username= username;
    }
    public String getUsername() {
        return username;
    }
}
