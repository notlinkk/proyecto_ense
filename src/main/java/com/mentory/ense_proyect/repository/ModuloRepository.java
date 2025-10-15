package com.mentory.ense_proyect.repository;

import com.mentory.ense_proyect.model.Modulo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.jspecify.annotations.NonNull;

@Repository
public interface ModuloRepository extends MongoRepository<@NonNull Modulo, @NonNull String> {
}
