package com.nbr.bankingsystem.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@Data
public class PastOrPresentDateValidator implements ConstraintValidator<PastOrPresentDate, String> {

    @Override
    public void initialize(PastOrPresentDate constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true; // Let @NotBlank handle null or empty values
        }
        try {
            LocalDate date = LocalDate.parse(value);
            return !date.isAfter(LocalDate.now());
        } catch (DateTimeParseException e) {
            return false; // Invalid date format
        }
    }
}
