package ru.practicum.shareit.booking.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class BookingValidationTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void bookItemRequestDto_WithNullItemId_ShouldFailValidation() {
        BookItemRequestDto dto = new BookItemRequestDto(
                null, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2)
        );

        Set<ConstraintViolation<BookItemRequestDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("Item ID is required")));
    }

    @Test
    void bookItemRequestDto_WithPastStartDate_ShouldFailValidation() {
        BookItemRequestDto dto = new BookItemRequestDto(
                1L, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1)
        );

        Set<ConstraintViolation<BookItemRequestDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("Start date must be in present or future")));
    }

    @Test
    void bookItemRequestDto_WithPastEndDate_ShouldFailValidation() {
        BookItemRequestDto dto = new BookItemRequestDto(
                1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().minusDays(1)
        );

        Set<ConstraintViolation<BookItemRequestDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("End date must be in future")));
    }

    @Test
    void bookItemRequestDto_WithNullStartDate_ShouldFailValidation() {
        BookItemRequestDto dto = new BookItemRequestDto(
                1L, null, LocalDateTime.now().plusDays(1)
        );

        Set<ConstraintViolation<BookItemRequestDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("Start date is required")));
    }

    @Test
    void bookItemRequestDto_WithNullEndDate_ShouldFailValidation() {
        BookItemRequestDto dto = new BookItemRequestDto(
                1L, LocalDateTime.now().plusDays(1), null
        );

        Set<ConstraintViolation<BookItemRequestDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("End date is required")));
    }

    @Test
    void bookItemRequestDto_WithValidData_ShouldPassValidation() {
        BookItemRequestDto dto = new BookItemRequestDto(
                1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2)
        );

        Set<ConstraintViolation<BookItemRequestDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }
}