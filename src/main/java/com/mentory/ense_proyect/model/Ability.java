package com.mentory.ense_proyect.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="abilities")
/*
    *
 */
public record Ability(
        @Id String name,
        String description
)  {

}
