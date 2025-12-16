package com.mentory.ense_proyect.model.dto;

/**
 * DTO para crear un módulo dentro de una lección.
 * Solo contiene atributos simples.
 * El lessonId se usa para vincular el módulo a su lección.
 */
public record ModuleDTO(
    String title,
    String description,
    String content,
    int duration,
    String lessonId  // ID de la lección a la que pertenece
) {}
