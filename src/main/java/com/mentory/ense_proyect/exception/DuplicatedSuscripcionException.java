package com.mentory.ense_proyect.exception;

import com.mentory.ense_proyect.model.Suscripcion;

public class DuplicatedSuscripcionException extends Throwable {
    private final Suscripcion suscripcion;

    public DuplicatedSuscripcionException(Suscripcion suscripcion) {
        this.suscripcion = suscripcion;
    }

    public Suscripcion getSuscripcion() {
        return suscripcion;
    }
}
