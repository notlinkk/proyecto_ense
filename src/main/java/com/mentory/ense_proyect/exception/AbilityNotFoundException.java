package com.mentory.ense_proyect.exception;

public class AbilityNotFoundException extends Throwable {
    private final String name;
    public AbilityNotFoundException(String name) {
        super("Habilidad no encontrada con el id: " + name);
        this.name = name;
    }
    public String getName() {
        return name;
    }
}
