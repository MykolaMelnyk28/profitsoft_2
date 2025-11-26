package com.melnyk.profitsoft_2.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ResourceAlreadyExistsException extends ResourceException {

    private final Object value;

    public ResourceAlreadyExistsException(Object id, Object value) {
        super(id);
        this.value = value;
    }

    public ResourceAlreadyExistsException(String message, Object id, Object value) {
        super(message, id);
        this.value = value;
    }

    public ResourceAlreadyExistsException(String message, Throwable cause, Object id, Object value) {
        super(message, cause, id);
        this.value = value;
    }

    public ResourceAlreadyExistsException(Throwable cause, Object id, Object value) {
        super(cause, id);
        this.value = value;
    }

    public ResourceAlreadyExistsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, Object id, Object value) {
        super(message, cause, enableSuppression, writableStackTrace, id);
        this.value = value;
    }

}
