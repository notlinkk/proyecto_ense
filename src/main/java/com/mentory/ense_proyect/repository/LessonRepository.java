package com.mentory.ense_proyect.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mentory.ense_proyect.model.entity.Lesson;

import org.jspecify.annotations.NonNull;

@Repository
public interface LessonRepository extends JpaRepository <@NonNull Lesson, @NonNull String> {
    
    /**
     * Find lessons by name containing the given string (case insensitive).
     */
    Page<Lesson> findByNameContainingIgnoreCase(String name, Pageable pageable);
    
    /**
     * Find lessons by owner ID.
     */
    Page<Lesson> findByOwnerId(String ownerId, Pageable pageable);
}
