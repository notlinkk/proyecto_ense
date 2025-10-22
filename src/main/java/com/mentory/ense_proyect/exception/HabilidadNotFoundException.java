package com.mentory.ense_proyect.exception;

public class HabilidadNotFoundException extends Throwable {
    private final String nombre;
    public HabilidadNotFoundException(String nombre) {
        super("Habilidad no encontrada con el id: " + nombre);
        this.nombre = nombre;
    }
    public String getNombre() {
        return nombre;
    }
}
