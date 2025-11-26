package com.melnyk.profitsoft_2.exception;

public class ResourceException extends RuntimeException {

    private final Object id;

    public ResourceException(Object id) {
        this.id = id;
    }

    public ResourceException(String message, Object id) {
        super(message);
        this.id = id;
    }

    public ResourceException(String message, Throwable cause, Object id) {
        super(message, cause);
        this.id = id;
    }

    public ResourceException(Throwable cause, Object id) {
        super(cause);
        this.id = id;
    }

    public ResourceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, Object id) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.id = id;
    }

}
