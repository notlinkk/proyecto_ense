package com.mentory.ense_proyect.exception;

public class UserNotFoundException extends Throwable {
    private final String username;
    public UserNotFoundException(String username) {
        super("Usuario no encontrado con el id: " + username);
        this.username= username;
    }
    public String getUsername() {
        return username;
    }
}
