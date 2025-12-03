package com.melnyk.profitsoft_2.util;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.databind.JsonMappingException;
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

    public static String extractShortMessage(Exception ex) {
        String description;
        String path = "";
        String coords = "";

        if (ex instanceof JsonMappingException jme) {
            String msg = jme.getOriginalMessage();

            if (msg != null) {
                if (msg.contains("Cannot deserialize")) {
                    description = "Expected different type";
                } else if (msg.contains("Missing")) {
                    description = "Missing required field";
                } else if (msg.contains("Unrecognized field")) {
                    description = "Unknown field";
                } else if (msg.contains("not one of the values accepted for Enum")) {
                    description = "Invalid value";
                } else {
                    description = "Mapping error";
                }
            } else {
                description = "Mapping error";
            }

            if (!jme.getPath().isEmpty()) {
                StringBuilder sb = new StringBuilder();
                jme.getPath().forEach(ref -> sb.append(".").append(ref.getFieldName()));
                path = " at $" + sb;
            }

            JsonLocation loc = jme.getLocation();
            if (loc != null) {
                coords = " [line:" + loc.getLineNr() + ", col:" + loc.getColumnNr() + "]";
            }

        } else if (ex instanceof com.fasterxml.jackson.core.JsonParseException jpe) {
            description = "Invalid JSON syntax";
            JsonLocation loc = jpe.getLocation();
            if (loc != null) {
                coords = " [line:" + loc.getLineNr() + ", col:" + loc.getColumnNr() + "]";
            }
        } else {
            description = "JSON processing error";
        }

        return description + path + coords;
    }

}
