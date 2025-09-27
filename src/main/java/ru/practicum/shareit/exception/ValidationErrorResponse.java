package ru.practicum.shareit.exception;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class ValidationErrorResponse {
    private List<Violation> violations = new ArrayList<>();

    @Data
    public static class Violation {
        private final String fieldName;
        private final String message;
    }
}