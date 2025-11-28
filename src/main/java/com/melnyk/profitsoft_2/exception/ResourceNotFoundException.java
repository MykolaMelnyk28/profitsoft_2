package com.melnyk.profitsoft_2.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends ResourceException {

    public ResourceNotFoundException(String message, Object id, String resourceName) {
        super(message, id, resourceName);
    }

}
