package com.mentory.ense_proyect.exception;

import com.mentory.ense_proyect.model.Habilidad;

public class DuplicatedHabilidadException extends Throwable  {
    private final Habilidad habilidad;

    public DuplicatedHabilidadException(Habilidad habilidad) {
        this.habilidad = habilidad;
    }

    public Habilidad getHabilidad() {
        return habilidad;
    }

}
