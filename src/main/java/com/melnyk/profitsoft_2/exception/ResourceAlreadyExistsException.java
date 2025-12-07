package com.melnyk.profitsoft_2.exception;

import com.melnyk.profitsoft_2.controller.GlobalExceptionHandler;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * The exception thrown when the passed request data conflicts with the data in the database.
 * {@link GlobalExceptionHandler} handled the exception like {@code 409} HTTP status
 */
@Getter
@ResponseStatus(HttpStatus.CONFLICT)
public class ResourceAlreadyExistsException extends ResourceException {

    private final Object value;

    public ResourceAlreadyExistsException(String message, Object id, String resourceName, Object value) {
        super(message, id, resourceName);
        this.value = value;
    }

}
