package com.mentory.ense_proyect.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mentory.ense_proyect.model.entity.Module;

import org.jspecify.annotations.NonNull;

import java.util.Optional;

@Repository
public interface ModuleRepository extends JpaRepository <@NonNull Module, @NonNull String> {
    
    /**
     * Find a module by ID with its lesson eagerly loaded.
     */
    @Query("SELECT m FROM Module m LEFT JOIN FETCH m.lesson WHERE m.id = :id")
    Optional<Module> findByIdWithLesson(@Param("id") String id);

    /**
     * Find all modules that the user has access to.
     * User has access if they own the lesson or have an active subscription.
     */
    @Query("""
        SELECT m FROM Module m 
        JOIN m.lesson l 
        WHERE l.ownerId = :username 
        OR EXISTS (
            SELECT s FROM Subscription s 
            WHERE s.buyer.username = :username 
            AND s.lesson.id = l.id 
            AND s.active = true
        )
        """)
    Page<Module> findAccessibleModules(@Param("username") String username, Pageable pageable);
}
