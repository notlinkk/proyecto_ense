package com.mentory.ense_proyect.model.dto;

/**
 * DTO para crear una lección.
 * Solo contiene atributos simples, sin relaciones complejas.
 * El ID se genera automáticamente si no se proporciona.
 */
public record LessonDTO(
    String name,
    String description
) {}
