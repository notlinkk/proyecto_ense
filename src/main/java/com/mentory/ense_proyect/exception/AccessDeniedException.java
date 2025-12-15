package com.mentory.ense_proyect.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a user attempts to access a resource they don't have permission for.
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class AccessDeniedException extends RuntimeException {
    
    public AccessDeniedException(String message) {
        super(message);
    }
    
    public AccessDeniedException(String resourceType, String resourceId) {
        super("Access denied to " + resourceType + " with id: " + resourceId);
    }
}
