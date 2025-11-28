package com.melnyk.profitsoft_2.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(HttpStatus.CONFLICT)
public class ResourceAlreadyExistsException extends ResourceException {

    private final Object value;

    public ResourceAlreadyExistsException(String message, Object id, String resourceName, Object value) {
        super(message, id, resourceName);
        this.value = value;
    }

}
