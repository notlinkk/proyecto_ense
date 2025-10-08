package com.mentory.ense_proyect.repository;

import com.mentory.ense_proyect.model.Habilidad;
import org.jspecify.annotations.NonNull;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HabilidadRepository extends MongoRepository<@NonNull Habilidad, @NonNull String> {}
