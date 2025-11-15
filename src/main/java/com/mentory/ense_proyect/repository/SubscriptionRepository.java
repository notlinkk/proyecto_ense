package com.mentory.ense_proyect.repository;

import com.mentory.ense_proyect.model.Subscription;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.jspecify.annotations.NonNull;

@Repository
public interface SubscriptionRepository extends MongoRepository<@NonNull Subscription, @NonNull String> {
}
