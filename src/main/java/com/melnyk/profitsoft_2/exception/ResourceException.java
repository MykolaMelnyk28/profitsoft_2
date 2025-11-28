package com.melnyk.profitsoft_2.exception;

import lombok.Getter;

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
