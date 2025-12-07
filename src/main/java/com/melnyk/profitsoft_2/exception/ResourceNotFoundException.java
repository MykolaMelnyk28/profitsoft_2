package com.melnyk.profitsoft_2.exception;

import com.melnyk.profitsoft_2.controller.GlobalExceptionHandler;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * The exception thrown when a some resource is not found.
 * {@link GlobalExceptionHandler} handled the exception like {@code 404} HTTP status
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends ResourceException {

    public ResourceNotFoundException(String message, Object id, String resourceName) {
        super(message, id, resourceName);
    }

}
