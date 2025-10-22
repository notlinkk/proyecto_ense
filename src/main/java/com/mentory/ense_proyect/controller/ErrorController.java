package com.mentory.ense_proyect.controller;

import com.mentory.ense_proyect.exception.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class ErrorController extends ResponseEntityExceptionHandler {

    @ExceptionHandler(HabilidadNotFoundException.class)
    public ErrorResponse handle(HabilidadNotFoundException ex) {
        ProblemDetail error = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        error.setDetail("La habilidad '"+ex.getNombre()+"' no se encuentra en la base de datos. Verifica el nombre e inténtalo de nuevo.");
        error.setType(MvcUriComponentsBuilder.fromController(ErrorController.class).pathSegment("error", "habilidad-no-encontrada").build().toUri());
        error.setTitle("Habilidad "+ex.getNombre()+" no encontrada");

        return ErrorResponse.builder(ex, error).build();
    }

    @ExceptionHandler(DuplicatedHabilidadException.class)
    public ErrorResponse handle(DuplicatedHabilidadException ex) {
        ProblemDetail error = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        error.setDetail("Ya existe una habilidad con el nombre '"+ex.getHabilidad().nombre()+"' en la base de datos: "+ex.getHabilidad());
        error.setType(MvcUriComponentsBuilder.fromController(ErrorController.class).pathSegment("error", "habilidad-duplicada").build().toUri());
        error.setTitle("Habilidad "+ex.getHabilidad().nombre()+" ya existe");

        return ErrorResponse.builder(ex, error)
                .header(HttpHeaders.LOCATION, MvcUriComponentsBuilder.fromMethodName(
                                HabilidadController.class,
                                "getHabilidad",
                                ex.getHabilidad().nombre()
                        ).build().toUriString())
                .build();
    }

    @ExceptionHandler(LeccionNotFoundException.class)
    public ErrorResponse handle(LeccionNotFoundException ex) {
        ProblemDetail error = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        error.setDetail("La lección con id="+ex.getId()+" no se encuentra en la base de datos. Verifica el identificador.");
        error.setType(MvcUriComponentsBuilder.fromController(ErrorController.class).pathSegment("error", "leccion-no-encontrada").build().toUri());
        error.setTitle("Lección "+ex.getId()+" no encontrada");

        return ErrorResponse.builder(ex, error).build();
    }

    @ExceptionHandler(DuplicatedLeccionException.class)
    public ErrorResponse handle(DuplicatedLeccionException ex) {
        ProblemDetail error = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        error.setDetail("Ya existe una lección con id="+ex.getLeccion().getId()+" en la base de datos: "+ex.getLeccion());
        error.setType(MvcUriComponentsBuilder.fromController(ErrorController.class).pathSegment("error", "leccion-duplicada").build().toUri());
        error.setTitle("Lección "+ex.getLeccion().getId()+" ya existe");

        return ErrorResponse.builder(ex, error)
                .header(HttpHeaders.LOCATION,
                        MvcUriComponentsBuilder.fromMethodName(
                                LeccionController.class,
                                "getLeccion",
                                ex.getLeccion().getId()
                        ).build().toUriString())
                .build();
    }

    @ExceptionHandler(ModuloNotFoundException.class)
    public ErrorResponse handle(ModuloNotFoundException ex) {
        ProblemDetail error = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        error.setDetail("El módulo con id="+ex.getId()+" no se encuentra en la base de datos.");
        error.setType(MvcUriComponentsBuilder.fromController(ErrorController.class)
                .pathSegment("error", "modulo-no-encontrado").build().toUri());
        error.setTitle("Módulo "+ex.getId()+" no encontrado");

        return ErrorResponse.builder(ex, error).build();
    }

    @ExceptionHandler(DuplicatedModuloException.class)
    public ErrorResponse handle(DuplicatedModuloException ex) {
        ProblemDetail error = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        error.setDetail("Ya existe un módulo con id="+ex.getModulo().getId()+" en la base de datos: "+ex.getModulo());
        error.setType(MvcUriComponentsBuilder.fromController(ErrorController.class)
                .pathSegment("error", "modulo-duplicado").build().toUri());
        error.setTitle("Módulo "+ex.getModulo().getId()+" ya existe");

        return ErrorResponse.builder(ex, error)
                .header(HttpHeaders.LOCATION,
                        MvcUriComponentsBuilder.fromMethodName(
                                ModuloController.class,
                                "getModulo",
                                ex.getModulo().getId()
                        ).build().toUriString())
                .build();
    }

    @ExceptionHandler(SuscripcionNotFoundException.class)
    public ErrorResponse handle(SuscripcionNotFoundException ex) {
        ProblemDetail error = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        error.setDetail("La suscripción con id="+ex.getId()+" no se encuentra en la base de datos.");
        error.setType(MvcUriComponentsBuilder.fromController(ErrorController.class)
                .pathSegment("error", "suscripcion-no-encontrada").build().toUri());
        error.setTitle("Suscripción "+ex.getId()+" no encontrada");

        return ErrorResponse.builder(ex, error).build();
    }

    @ExceptionHandler(DuplicatedSuscripcionException.class)
    public ErrorResponse handle(DuplicatedSuscripcionException ex) {
        ProblemDetail error = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        error.setDetail("Ya existe una suscripción con id="+ex.getSuscripcion().getId()+" en la base de datos: "+ex.getSuscripcion());
        error.setType(MvcUriComponentsBuilder.fromController(ErrorController.class)
                .pathSegment("error", "suscripcion-duplicada").build().toUri());
        error.setTitle("Suscripción "+ex.getSuscripcion().getId()+" ya existe");

        return ErrorResponse.builder(ex, error)
                .header(HttpHeaders.LOCATION,
                        MvcUriComponentsBuilder.fromMethodName(
                                SuscripcionController.class,
                                "getSuscripcion",
                                ex.getSuscripcion().getId()
                        ).build().toUriString())
                .build();
    }

    @ExceptionHandler(UsuarioNotFoundException.class)
    public ErrorResponse handle(UsuarioNotFoundException ex) {
        ProblemDetail error = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        error.setDetail("El usuario "+ex.getUsername()+" no se encuentra en la base de datos. Verifica el nombre de usuario.");
        error.setType(MvcUriComponentsBuilder.fromController(ErrorController.class)
                .pathSegment("error", "usuario-no-encontrado").build().toUri());
        error.setTitle("Usuario "+ex.getUsername()+" no encontrado");

        return ErrorResponse.builder(ex, error).build();
    }

    @ExceptionHandler(DuplicatedUsuarioException.class)
    public ErrorResponse handle(DuplicatedUsuarioException ex) {
        ProblemDetail error = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        error.setDetail("Ya existe un usuario con nombre "+ex.getUsuario().getUsername()+" en la base de datos: "+ex.getUsuario());
        error.setType(MvcUriComponentsBuilder.fromController(ErrorController.class)
                .pathSegment("error", "usuario-duplicado").build().toUri());
        error.setTitle("Usuario "+ex.getUsuario().getUsername()+" ya existe");

        return ErrorResponse.builder(ex, error)
                .header(HttpHeaders.LOCATION,
                        MvcUriComponentsBuilder.fromMethodName(
                                UsuarioController.class,
                                "getUsuario",
                                ex.getUsuario().getUsername()
                        ).build().toUriString())
                .build();
    }
}
