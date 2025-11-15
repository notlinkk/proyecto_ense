package com.mentory.ense_proyect.exception;

import com.mentory.ense_proyect.model.Lesson;

public class DuplicatedLessonException extends Throwable  {
    private final Lesson lesson;

    public DuplicatedLessonException(Lesson Lesson) {
        this.lesson = Lesson;
    }

    public Lesson getLesson() {
        return lesson;
    }
}
