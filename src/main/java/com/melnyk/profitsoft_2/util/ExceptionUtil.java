package com.melnyk.profitsoft_2.util;

import com.melnyk.profitsoft_2.exception.ResourceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.stream.Collectors;

public final class ExceptionUtil {
    private ExceptionUtil() {}

    public static ProblemDetail buildProblemDetail(HttpStatus status, String title, String detail) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setTitle(title);
        return problem;
    }

    public static void appendErrorsProperty(ProblemDetail problemDetail, BindingResult result) {
        problemDetail.setProperty("errors", result
            .getFieldErrors()
            .stream()
            .collect(Collectors.groupingBy(
                FieldError::getField,
                Collectors.mapping(FieldError::getDefaultMessage, Collectors.toSet())
            ))
        );
    }

    public static void appendResourceInfo(ProblemDetail problemDetail, ResourceException re) {
        problemDetail.setProperty("resource", re.getResourceName());
        problemDetail.setProperty("id", re.getId());
    }

}
