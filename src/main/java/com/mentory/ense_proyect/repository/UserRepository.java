package com.mentory.ense_proyect.repository;

import com.mentory.ense_proyect.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.jspecify.annotations.NonNull;

@Repository
public interface UserRepository extends MongoRepository<@NonNull User, @NonNull String> {
}
