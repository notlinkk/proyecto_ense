package com.mentory.ense_proyect.repository;

import com.mentory.ense_proyect.model.Leccion;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.jspecify.annotations.NonNull;

@Repository
public interface LeccionRepository extends MongoRepository<@NonNull Leccion, @NonNull String> {
}
