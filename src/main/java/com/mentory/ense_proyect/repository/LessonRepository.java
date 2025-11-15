package com.mentory.ense_proyect.repository;

import com.mentory.ense_proyect.model.Lesson;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.jspecify.annotations.NonNull;

@Repository
public interface LessonRepository extends MongoRepository<@NonNull Lesson, @NonNull String> {
}
