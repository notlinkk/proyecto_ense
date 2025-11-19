package com.mentory.ense_proyect.repository;

import com.mentory.ense_proyect.model.Lesson;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.jspecify.annotations.NonNull;

@Repository
public interface LessonRepository extends JpaRepository <@NonNull Lesson, @NonNull String> {
}
