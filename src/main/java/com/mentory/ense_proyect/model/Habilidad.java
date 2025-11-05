package com.mentory.ense_proyect.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="habilidades")
/*
    *
 */
public record Habilidad(
        @Id String nombre,
        String descripcion
)  {

}
