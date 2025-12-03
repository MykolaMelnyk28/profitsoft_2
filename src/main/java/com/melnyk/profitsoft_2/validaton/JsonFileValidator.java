package com.melnyk.profitsoft_2.validaton;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class JsonFileValidator implements ConstraintValidator<JsonFile, MultipartFile> {

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        if (file == null || file.isEmpty()) {
            return true;
        }
        if (file.getContentType() == null) {
            return false;
        }

        try {
            MediaType fileType = MediaType.parseMediaType(file.getContentType());
            MediaType expectedType = MediaType.APPLICATION_JSON;
            if (!expectedType.includes(fileType)) {
                return false;
            }
        } catch (InvalidMediaTypeException e) {
            return false;
        }

        return true;
    }

}
