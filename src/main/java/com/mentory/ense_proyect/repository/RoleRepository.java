package com.mentory.ense_proyect.repository;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import com.mentory.ense_proyect.model.Role;

@Repository
@NullMarked
public interface RoleRepository extends MongoRepository<@NonNull Role, @NonNull String> {
}