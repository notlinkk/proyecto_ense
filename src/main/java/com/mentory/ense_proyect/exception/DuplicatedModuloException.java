package com.mentory.ense_proyect.exception;

import com.mentory.ense_proyect.model.Modulo;

public class DuplicatedModuloException extends Throwable  {
    private final Modulo modulo;

    public DuplicatedModuloException(Modulo modulo) {
        this.modulo = modulo;
    }

    public Modulo getModulo() {
        return modulo;
    }
}
