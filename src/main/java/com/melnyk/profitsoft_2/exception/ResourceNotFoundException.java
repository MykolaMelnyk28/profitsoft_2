package com.melnyk.profitsoft_2.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends ResourceException {

    public ResourceNotFoundException(Object id) {
        super(id);
    }

    public ResourceNotFoundException(String message, Object id) {
        super(message, id);
    }

    public ResourceNotFoundException(String message, Throwable cause, Object id) {
        super(message, cause, id);
    }

    public ResourceNotFoundException(Throwable cause, Object id) {
        super(cause, id);
    }

    public ResourceNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, Object id) {
        super(message, cause, enableSuppression, writableStackTrace, id);
    }

}
