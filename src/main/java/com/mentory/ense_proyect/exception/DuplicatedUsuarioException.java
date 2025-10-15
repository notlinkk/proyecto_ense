package com.mentory.ense_proyect.exception;

import com.mentory.ense_proyect.model.Usuario;

public class DuplicatedUsuarioException extends Throwable {
    private final Usuario usuario;

    public DuplicatedUsuarioException(Usuario usuario) {
        this.usuario = usuario;
    }

    public Usuario getUsuario() {
        return usuario;
    }
}
