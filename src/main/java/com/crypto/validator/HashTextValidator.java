package com.crypto.validator;

import com.crypto.annotation.HashText;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

@Slf4j
public class HashTextValidator implements ConstraintValidator<HashText, String> {
    private String fieldName;
    private String[] acceptValues;

    @Override
    public void initialize(HashText constraintAnnotation) {
        this.fieldName = constraintAnnotation.fieldName();
        this.acceptValues = constraintAnnotation.acceptValues();
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (!StringUtils.hasText(value)) {
            return false;
        }

        // Validate acceptValues
        if (!CollectionUtils.isEmpty(List.of(acceptValues))) {
            for (String item : List.of(acceptValues)) {
                if (item.equals(value)) {
                    return true;
                }
            }

            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate(String.format("%s only accept %s", this.fieldName, this.acceptValues)).addConstraintViolation();
            return false;
        }

        return true;
    }
}
