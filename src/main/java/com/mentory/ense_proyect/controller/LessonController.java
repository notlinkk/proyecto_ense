package com.mentory.ense_proyect.controller;

import com.mentory.ense_proyect.exception.LessonNotFoundException;
import com.mentory.ense_proyect.exception.DuplicatedLessonException;
import com.mentory.ense_proyect.model.Lesson;
import com.mentory.ense_proyect.repository.LessonRepository;
import com.mentory.ense_proyect.service.LessonService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.JsonPatchOperation;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("lessons")
public class LessonController {
    private final LessonRepository lessonRepository;
    LessonService lessonService;

    @Autowired
    public LessonController(LessonService lessonService, LessonRepository lessonRepository) {
        this.lessonService = lessonService;
        this.lessonRepository = lessonRepository;
    }

    @GetMapping("{id}")
    public ResponseEntity <Lesson> getLesson(@PathVariable("id") String id) throws LessonNotFoundException {
        return ResponseEntity.ok(lessonService.getLesson(id));
    }

    @GetMapping
    public ResponseEntity<Page<Lesson>> getLessons(
            @RequestParam(value="nombre", required=false) String nombre,
            @RequestParam(value="page", required=false, defaultValue="0") int page,
            @RequestParam(value="size", required=false, defaultValue="2") int pagesize,
            @RequestParam(value="sort", required=false, defaultValue="") List<String> sort
    ) {
        Page<Lesson> lessons = lessonService.getLessons(
                nombre,
                PageRequest.of(
                        page, pagesize,
                        Sort.by(sort.stream()
                                .map(key -> key.startsWith("-") ?
                                        Sort.Order.desc(key.substring(1)) :
                                        Sort.Order.asc(key))
                                .toList()
                        )
                )
        );

        if (lessons.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(lessons);
    }

    @PostMapping
    public ResponseEntity<Lesson> createLesson(@RequestBody Lesson lesson) throws DuplicatedLessonException {
        Lesson newLesson = lessonService.addLesson(lesson);
        return ResponseEntity
                .created(MvcUriComponentsBuilder
                        .fromMethodName(LessonController.class, "getLesson", lesson.getId())
                        .build()
                        .toUri())
                .body(newLesson);
    }

    @DeleteMapping({"{id}"})
    public ResponseEntity<Void> deleteLesson(@PathVariable("id") String id) throws LessonNotFoundException {
        lessonService.deleteLesson(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("{id}" )
    public ResponseEntity<Lesson> updateLesson(
            @PathVariable("id") String id,
            @RequestBody List<JsonPatchOperation> changes
    ) throws LessonNotFoundException, JsonPatchException {
        return ResponseEntity.ok(lessonService.updateLesson(id, changes));
    }

}
