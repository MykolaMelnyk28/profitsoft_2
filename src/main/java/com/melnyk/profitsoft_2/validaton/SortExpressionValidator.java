package com.melnyk.profitsoft_2.validaton;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class SortExpressionValidator implements ConstraintValidator<SortExpression, String> {

    private static final Pattern PATTERN = Pattern.compile("(?i)(\\w+)\\s*,\\s*(asc|desc)");

    public Class<?> targetType;

    @Override
    public void initialize(SortExpression constraintAnnotation) {
        this.targetType = constraintAnnotation.targetType();
    }

    @Override
    public boolean isValid(String sort, ConstraintValidatorContext constraintValidatorContext) {
        if (sort == null) {
            return true;
        }

        Matcher matcher = PATTERN.matcher(sort);
        if (!matcher.matches()) {
            return false;
        }

        String property = matcher.group(1);
        return ReflectionUtils.findFieldIgnoreCase(targetType, property) != null;
    }

}