package com.mentory.ense_proyect.model.dto;

/**
 * DTO para devolver una suscripción con información de la lección.
 * Se usa para evitar problemas de serialización circular.
 */
public record SubscriptionResponseDTO(
    String id,
    String startDate,
    String endDate,
    boolean active,
    LessonSummaryDTO lesson
) {
    /**
     * DTO resumido de una lección para incluir en la respuesta de suscripción.
     */
    public record LessonSummaryDTO(
        String id,
        String name,
        String description,
        double price
    ) {}
}
