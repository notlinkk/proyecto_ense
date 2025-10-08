package com.mentory.ense_proyect.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="habilidades")
public record Habilidad(
    @Id String id,
    String nombre,
    String descripcion
)  {
    // Constructor sin id para crear nuevas habilidades
    public Habilidad(String nombre, String descripcion) {
        this(null, nombre, descripcion);
    }
}
