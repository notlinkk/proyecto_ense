package com.mentory.ense_proyect.repository;

import com.mentory.ense_proyect.model.Subscription;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.jspecify.annotations.NonNull;

@Repository
public interface SubscriptionRepository extends JpaRepository <@NonNull Subscription, @NonNull String> {
}
