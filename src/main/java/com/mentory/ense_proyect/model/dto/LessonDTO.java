package com.mentory.ense_proyect.model.dto;

import java.util.Set;

/**
 * DTO para crear una lección.
 * Solo contiene atributos simples, sin relaciones complejas.
 * El ID se genera automáticamente si no se proporciona.
 * Debe incluir al menos una habilidad.
 */
public record LessonDTO(
    String name,
    String description,
    Double price,
    String imageUrl,
    Set<String> abilities  // Nombres de las habilidades
) {}
