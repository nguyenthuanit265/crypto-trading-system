package com.crypto.annotation;

import com.crypto.validator.HashTextValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = HashTextValidator.class)
public @interface HashText {
    String fieldName() default "";

    String[] acceptValues() default {};

    String message() default "{fieldName} {jakarta.validation.constraints.NotNull.message} || {fieldName} jakarta.validation.constraints.NotBlank.message";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
