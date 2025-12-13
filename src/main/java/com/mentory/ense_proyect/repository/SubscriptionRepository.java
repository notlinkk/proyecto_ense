package com.mentory.ense_proyect.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mentory.ense_proyect.model.entity.Subscription;

import org.jspecify.annotations.NonNull;

@Repository
public interface SubscriptionRepository extends JpaRepository <@NonNull Subscription, @NonNull String> {
    
    /**
     * Find all subscriptions for a specific user.
     */
    Page<Subscription> findByBuyerUsername(String username, Pageable pageable);
    
    /**
     * Find all subscriptions for a specific lesson.
     */
    Page<Subscription> findByLessonId(String lessonId, Pageable pageable);
    
    /**
     * Check if a user has an active subscription to a lesson.
     */
    Optional<Subscription> findByBuyerUsernameAndLessonIdAndActiveTrue(String username, String lessonId);
    
    /**
     * Check if a subscription exists for a user and lesson.
     */
    boolean existsByBuyerUsernameAndLessonId(String username, String lessonId);
}
