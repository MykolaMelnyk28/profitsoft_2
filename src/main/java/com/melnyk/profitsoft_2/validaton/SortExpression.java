package com.melnyk.profitsoft_2.validaton;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = SortExpressionValidator.class)
@Target( ElementType.FIELD )
@Retention(RetentionPolicy.RUNTIME)
public @interface SortExpression {
    Class<?> targetType();
    String message() default "invalid sort expression";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}