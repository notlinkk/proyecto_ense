package com.mentory.ense_proyect.controller;

import com.mentory.ense_proyect.exception.LessonNotFoundException;
import com.mentory.ense_proyect.model.dto.LessonDTO;
import com.mentory.ense_proyect.model.entity.Lesson;
import com.mentory.ense_proyect.exception.DuplicatedLessonException;
import com.mentory.ense_proyect.repository.LessonRepository;
import com.mentory.ense_proyect.service.LessonService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import org.springframework.hateoas.*;
import org.springframework.hateoas.server.EntityLinks;
import org.springframework.hateoas.server.ExposesResourceFor;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("lessons")
@ExposesResourceFor(Lesson.class)
public class LessonController {
    private final LessonRepository lessonRepository;
    LessonService lessonService;
    private EntityLinks entityLinks;
    @Autowired
    public LessonController(LessonService lessonService, LessonRepository lessonRepository, EntityLinks entityLinks) {
        this.lessonService = lessonService;
        this.lessonRepository = lessonRepository;
        this.entityLinks = entityLinks;
    }

    @GetMapping(path="{id}", headers = "API-Version=0")
    @PreAuthorize("hasAuthority('lessons:read')")
    public ResponseEntity <Lesson> getLessonV0(@PathVariable("id") String id, Authentication authentication) throws LessonNotFoundException {
        String username = authentication.getName();
        return ResponseEntity.ok(lessonService.getLessonWithAccessControl(id, username));
    }
    @GetMapping(path="{id}", headers = "API-Version=1")
    @PreAuthorize("hasAuthority('lessons:read')")
    public ResponseEntity <EntityModel<Lesson>> getLessonV1(@PathVariable("id") String id, Authentication authentication) throws LessonNotFoundException {
        String username = authentication.getName();
        EntityModel<Lesson> lesson = EntityModel.of(lessonService.getLessonWithAccessControl(id, username));
        lesson.add(
            entityLinks.linkToItemResource(Lesson.class, lesson.getContent().getId()).withSelfRel(),
            entityLinks.linkToCollectionResource(Lesson.class).withRel(IanaLinkRelations.COLLECTION),
            entityLinks.linkToItemResource(Lesson.class, lesson.getContent().getId()).withRel("delete").withType("DELETE")
        );
        return ResponseEntity.ok(lesson);
    }

    @GetMapping ( headers = "API-Version=0")
    @PreAuthorize("hasAuthority('lessons:read')")
    public ResponseEntity<Page<Lesson>> getLessonsV0(
            @RequestParam(value="nombre", required=false) String nombre,
            @RequestParam(value="page", required=false, defaultValue="0") int page,
            @RequestParam(value="size", required=false, defaultValue="10") int pagesize,
            @RequestParam(value="sort", required=false, defaultValue="") List<String> sort,
            Authentication authentication
    ) {
        String currentUserId = authentication != null ? authentication.getName() : null;
        Page<Lesson> lessons = lessonService.getLessons(
                nombre,
                currentUserId,
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

        return ResponseEntity.ok(lessons);
    }

    @GetMapping(headers = "API-Version=1")
    @PreAuthorize("hasAuthority('lessons:read')")
    public ResponseEntity<PagedModel<Lesson>> getLessonsV1(
            @RequestParam(value="nombre", required=false) String nombre,
            @RequestParam(value="page", required=false, defaultValue="0") int page,
            @RequestParam(value="size", required=false, defaultValue="2") int pagesize,
            @RequestParam(value="sort", required=false, defaultValue="") List<String> sort,
            Authentication authentication
    ) {
        String currentUserId = authentication != null ? authentication.getName() : null;
        Page<Lesson> lessons = lessonService.getLessons(
                nombre,
                currentUserId,
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

        PagedModel<Lesson> response= PagedModel.of(
            lessons.getContent(),
            new PagedModel.PageMetadata(
                lessons.getSize(),
                lessons.getNumber(),
                lessons.getTotalElements(),
                lessons.getTotalPages()
            )
        );

        response.add( entityLinks.linkToCollectionResource(Lesson.class).withSelfRel() );

        // PREVIOUS
        if (lessons.hasPrevious()) {
            response.add(
                    linkTo(methodOn(LessonController.class)
                            .getLessonsV1(nombre, page - 1, pagesize, sort, null))
                            .withRel(IanaLinkRelations.PREVIOUS)
            );
        }

        // NEXT
        if (lessons.hasNext()) {
            response.add(
                    linkTo(methodOn(LessonController.class)
                            .getLessonsV1(nombre, page + 1, pagesize, sort, null))
                            .withRel(IanaLinkRelations.NEXT)
            );
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('lessons:write')")
    public ResponseEntity<Lesson> createLesson(@RequestBody LessonDTO lessonDTO, Authentication authentication) throws DuplicatedLessonException {
        String ownerId = authentication.getName();
        Lesson newLesson = lessonService.createLesson(lessonDTO, ownerId);
        return ResponseEntity
                .created(MvcUriComponentsBuilder
                        .fromMethodName(LessonController.class, "getLessonV1", newLesson.getId(), null)
                        .build()
                        .toUri())
                .body(newLesson);
    }

    @DeleteMapping({"{id}"})
    @PreAuthorize("hasAuthority('lessons:delete')")
    public ResponseEntity<Void> deleteLesson(@PathVariable("id") String id) throws LessonNotFoundException {
        lessonService.deleteLesson(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("{id}" )
    @PreAuthorize("hasAuthority('lessons:update')")
    public ResponseEntity<Lesson> updateLesson(
            @PathVariable("id") String id,
            @RequestBody Map<String, Object> changes
    ) throws LessonNotFoundException {
        return ResponseEntity.ok(lessonService.updateLesson(id, changes));
    }

}
