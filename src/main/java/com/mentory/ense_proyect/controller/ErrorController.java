package com.mentory.ense_proyect.controller;

import com.github.fge.jsonpatch.JsonPatchException;
import com.mentory.ense_proyect.exception.*;
import com.mentory.ense_proyect.model.entity.Ability;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class ErrorController extends ResponseEntityExceptionHandler {

    @ExceptionHandler(AbilityNotFoundException.class)
    public ErrorResponse handle(AbilityNotFoundException ex) {
        ProblemDetail error = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        error.setDetail("La habilidad '"+ex.getName()+"' no se encuentra en la base de datos. Verifica el nombre e inténtalo de nuevo.");
        error.setType(MvcUriComponentsBuilder.fromController(ErrorController.class).pathSegment("error", "habilidad-no-encontrada").build().toUri());
        error.setTitle("Habilidad "+ex.getName()+" no encontrada");

        return ErrorResponse.builder(ex, error).build();
    }

    @ExceptionHandler(DuplicatedAbilityException.class)
    public ErrorResponse handle(DuplicatedAbilityException ex) {
        ProblemDetail error = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        error.setDetail("Ya existe una habilidad con el nombre '"+ex.getAbility().getName()+"' en la base de datos: "+ex.getAbility());
        error.setType(MvcUriComponentsBuilder.fromController(ErrorController.class).pathSegment("error", "habilidad-duplicada").build().toUri());
        error.setTitle("Habilidad "+ex.getAbility().getName()+" ya existe");

        return ErrorResponse.builder(ex, error)
                .header(HttpHeaders.LOCATION, MvcUriComponentsBuilder.fromMethodName(
                                AbilityController.class,
                                "getHabilidad",
                                ex.getAbility().getName()
                        ).build().toUriString())
                .build();
    }

    @ExceptionHandler(LessonNotFoundException.class)
    public ErrorResponse handle(LessonNotFoundException ex) {
        ProblemDetail error = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        error.setDetail("La lección con id="+ex.getId()+" no se encuentra en la base de datos. Verifica el identificador.");
        error.setType(MvcUriComponentsBuilder.fromController(ErrorController.class).pathSegment("error", "leccion-no-encontrada").build().toUri());
        error.setTitle("Lección "+ex.getId()+" no encontrada");

        return ErrorResponse.builder(ex, error).build();
    }

    @ExceptionHandler(DuplicatedLessonException.class)
    public ErrorResponse handle(DuplicatedLessonException ex) {
        ProblemDetail error = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        error.setDetail("Ya existe una lección con id="+ex.getLesson().getId()+" en la base de datos: "+ex.getLesson());
        error.setType(MvcUriComponentsBuilder.fromController(ErrorController.class).pathSegment("error", "leccion-duplicada").build().toUri());
        error.setTitle("Lección "+ex.getLesson().getId()+" ya existe");

        return ErrorResponse.builder(ex, error)
                .header(HttpHeaders.LOCATION,
                        MvcUriComponentsBuilder.fromMethodName(
                                LessonController.class,
                                "getLeccion",
                                ex.getLesson().getId()
                        ).build().toUriString())
                .build();
    }

    @ExceptionHandler(ModuleNotFoundException.class)
    public ErrorResponse handle(ModuleNotFoundException ex) {
        ProblemDetail error = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        error.setDetail("El módulo con id="+ex.getId()+" no se encuentra en la base de datos.");
        error.setType(MvcUriComponentsBuilder.fromController(ErrorController.class)
                .pathSegment("error", "modulo-no-encontrado").build().toUri());
        error.setTitle("Módulo "+ex.getId()+" no encontrado");

        return ErrorResponse.builder(ex, error).build();
    }

    @ExceptionHandler(DuplicatedModuleException.class)
    public ErrorResponse handle(DuplicatedModuleException ex) {
        ProblemDetail error = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        error.setDetail("Ya existe un módulo con id="+ex.getModule().getId()+" en la base de datos: "+ex.getModule());
        error.setType(MvcUriComponentsBuilder.fromController(ErrorController.class)
                .pathSegment("error", "modulo-duplicado").build().toUri());
        error.setTitle("Módulo "+ex.getModule().getId()+" ya existe");

        return ErrorResponse.builder(ex, error)
                .header(HttpHeaders.LOCATION,
                        MvcUriComponentsBuilder.fromMethodName(
                                ModuleController.class,
                                "getModulo",
                                ex.getModule().getId()
                        ).build().toUriString())
                .build();
    }

    @ExceptionHandler(SubscriptionNotFoundException.class)
    public ErrorResponse handle(SubscriptionNotFoundException ex) {
        ProblemDetail error = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        error.setDetail("La suscripción con id="+ex.getId()+" no se encuentra en la base de datos.");
        error.setType(MvcUriComponentsBuilder.fromController(ErrorController.class)
                .pathSegment("error", "suscripcion-no-encontrada").build().toUri());
        error.setTitle("Suscripción "+ex.getId()+" no encontrada");

        return ErrorResponse.builder(ex, error).build();
    }

    @ExceptionHandler(DuplicatedSubscriptionException.class)
    public ErrorResponse handle(DuplicatedSubscriptionException ex) {
        ProblemDetail error = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        error.setDetail("Ya existe una suscripción con id="+ex.getSubscription().getId()+" en la base de datos: "+ex.getSubscription());
        error.setType(MvcUriComponentsBuilder.fromController(ErrorController.class)
                .pathSegment("error", "suscripcion-duplicada").build().toUri());
        error.setTitle("Suscripción "+ex.getSubscription().getId()+" ya existe");

        return ErrorResponse.builder(ex, error)
                .header(HttpHeaders.LOCATION,
                        MvcUriComponentsBuilder.fromMethodName(
                                SubscriptionController.class,
                                "getSuscripcion",
                                ex.getSubscription().getId()
                        ).build().toUriString())
                .build();
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ErrorResponse handle(UserNotFoundException ex) {
        ProblemDetail error = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        error.setDetail("El usuario "+ex.getUsername()+" no se encuentra en la base de datos. Verifica el nombre de usuario.");
        error.setType(MvcUriComponentsBuilder.fromController(ErrorController.class)
                .pathSegment("error", "usuario-no-encontrado").build().toUri());
        error.setTitle("Usuario "+ex.getUsername()+" no encontrado");

        return ErrorResponse.builder(ex, error).build();
    }

    @ExceptionHandler(DuplicatedUserException.class)
    public ErrorResponse handle(DuplicatedUserException ex) {
        ProblemDetail error = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        error.setDetail("Ya existe un usuario con nombre "+ex.getUser().getUsername()+" en la base de datos: "+ex.getUser());
        error.setType(MvcUriComponentsBuilder.fromController(ErrorController.class)
                .pathSegment("error", "usuario-duplicado").build().toUri());
        error.setTitle("Usuario "+ex.getUser().getUsername()+" ya existe");

        return ErrorResponse.builder(ex, error)
                .header(HttpHeaders.LOCATION,
                        MvcUriComponentsBuilder.fromMethodName(
                                UserController.class,
                                "getUserV1",
                                ex.getUser().getUsername()
                        ).build().toUriString())
                .build();
    }

    @ExceptionHandler(JsonPatchException.class)
    public ResponseEntity<String> handleJsonPatchException(JsonPatchException ex) {
        return ResponseEntity.badRequest().body("Error aplicando el parche JSON: " + ex.getMessage());
    }
}
