package com.melnyk.profitsoft_2.controller;

import com.melnyk.profitsoft_2.exception.ResourceAlreadyExistsException;
import com.melnyk.profitsoft_2.exception.ResourceException;
import com.melnyk.profitsoft_2.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.http.*;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import tools.jackson.core.JacksonException;

import static com.melnyk.profitsoft_2.util.ExceptionUtil.*;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ProblemDetail handleResourceNotFound(ResourceNotFoundException ex) {
        log.warn("[{}]: {} {} not found", ex.getClass().getName(), ex.getResourceName(), ex.getId());
        ProblemDetail problemDetail = buildProblemDetail(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage());
        appendResourceInfo(problemDetail, ex);
        return problemDetail;
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ProblemDetail handleResourceAlreadyExists(ResourceAlreadyExistsException ex) {
        log.warn("[{}]: {} {} duplicate unique value {}",
            ex.getClass().getName(),
            ex.getResourceName(),
            ex.getId(),
            ex.getValue());
        ProblemDetail problemDetail = buildProblemDetail(HttpStatus.CONFLICT, "Already Exists", ex.getMessage());
        appendResourceInfo(problemDetail, ex);
        return problemDetail;
    }

    @ExceptionHandler(ResourceException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handleResourceException(ResourceException ex) {
        log.warn("[{}]: {} {}. {}",
            ex.getClass().getName(),
            ex.getResourceName(),
            ex.getId(),
            ex.getMessage());
        ProblemDetail problemDetail = buildProblemDetail(HttpStatus.BAD_REQUEST, "Resource Error", ex.getMessage());
        appendResourceInfo(problemDetail, ex);
        return problemDetail;
    }

    @ExceptionHandler({
            IllegalArgumentException.class,
            IllegalStateException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handleBadRequest(Exception ex) {
        log.warn("[{}] {}",
            ex.getClass().getName(),
            ex.getMessage());
        return buildProblemDetail(HttpStatus.BAD_REQUEST, "Bad request", "Bad request");
    }

    @ExceptionHandler(JacksonException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handleJacksonException(JacksonException ex) {
        log.warn("[{}] {}",
            ex.getClass().getName(),
            ex.getMessage());
        String msg = extractShortMessage(ex);
        return buildProblemDetail(HttpStatus.BAD_REQUEST, "Bad request", msg);
    }

    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handleBindException(BindException ex) {
        log.warn("[{}]: {}",
            ex.getClass().getName(),
            ex.getMessage());
        ProblemDetail problemDetail = buildProblemDetail(HttpStatus.BAD_REQUEST, "Binding failed", "Invalid request data");
        appendErrorsProperty(problemDetail, ex.getBindingResult());
        return problemDetail;
    }

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ProblemDetail internalServerError(Exception ex, HttpServletRequest request) {
        log.error("Error processing [{} {}]: {}",
            request.getMethod(),
            request.getRequestURI(),
            ex.getMessage(),
            ex);
        return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
    }

    @Override
    protected @Nullable ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        log.warn("[{}]: {}",
            ex.getClass().getName(),
            ex.getMessage());
        ProblemDetail problemDetail = buildProblemDetail(HttpStatus.BAD_REQUEST, "Validation failed", "One or more fields have validation errors");
        appendErrorsProperty(problemDetail, ex.getBindingResult());
        return super.createResponseEntity(problemDetail, headers, status, request);
    }

}