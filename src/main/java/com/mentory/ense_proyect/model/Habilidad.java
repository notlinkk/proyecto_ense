package com.mentory.ense_proyect.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="habilidades")
public record Habilidad(
        // Se buscará un nombre único de una habilidad. No se pueden repetir.
    @Id String nombre,
    String descripcion
)  {

}
