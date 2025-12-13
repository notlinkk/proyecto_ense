package com.mentory.ense_proyect.model.dto;

/**
 * DTO para crear una suscripción.
 * Solo contiene el ID de la lección a la que suscribirse.
 * El usuario se obtiene del contexto de autenticación.
 */
public record SubscriptionDTO(
    String lessonId
) {}
