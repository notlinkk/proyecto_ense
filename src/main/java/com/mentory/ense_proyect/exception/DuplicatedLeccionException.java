package com.mentory.ense_proyect.exception;

import com.mentory.ense_proyect.model.Leccion;

public class DuplicatedLeccionException extends Throwable  {
    private final Leccion leccion;

    public DuplicatedLeccionException(Leccion leccion) {
        this.leccion = leccion;
    }

    public Leccion getLeccion() {
        return leccion;
    }
}
