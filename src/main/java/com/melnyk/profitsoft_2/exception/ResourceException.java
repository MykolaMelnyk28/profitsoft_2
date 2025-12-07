package com.melnyk.profitsoft_2.exception;

import com.melnyk.profitsoft_2.controller.GlobalExceptionHandler;
import lombok.Getter;

/**
 * The exception is super class for other resource exceptions.
 * {@link GlobalExceptionHandler} handled the exception like {@code 400} HTTP status
 */
@Getter
public class ResourceException extends RuntimeException {

    private final Object id;
    private final String resourceName;

    public ResourceException(String message, Object id, String resourceName) {
        super(message);
        this.id = id;
        this.resourceName = resourceName;
    }

}
