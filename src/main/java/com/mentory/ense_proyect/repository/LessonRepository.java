package com.mentory.ense_proyect.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mentory.ense_proyect.model.entity.Lesson;

import org.jspecify.annotations.NonNull;

import java.util.Optional;

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

    /**
     * Find a lesson by ID with modules and abilities eagerly loaded.
     */
    @Query("SELECT l FROM Lesson l LEFT JOIN FETCH l.modules LEFT JOIN FETCH l.abilities WHERE l.id = :id")
    Optional<Lesson> findByIdWithModulesAndAbilities(@Param("id") String id);

    /**
     * Find all lessons excluding those owned by a specific user.
     */
    Page<Lesson> findByOwnerIdNot(String ownerId, Pageable pageable);

    /**
     * Find lessons by name containing the given string, excluding those owned by a specific user.
     */
    Page<Lesson> findByNameContainingIgnoreCaseAndOwnerIdNot(String name, String ownerId, Pageable pageable);
}
