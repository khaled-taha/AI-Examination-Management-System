package com.university.exam.examManagement.dtos.request;

import lombok.Data;
import jakarta.validation.constraints.*;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
public class ExamQuestionCodingTestCaseRequestDTO {

    @NotNull(message = "Exam question ID is required")
    private UUID examQuestionId;
    private List<TestCase> testCases;

    @Data
    @ToString
    public static class TestCase {
        private UUID id;

        @NotBlank(message = "Test case input must not be blank")
        private String input;

        @NotBlank(message = "Expected output must not be blank")
        private String expectedOutput;

        @NotNull(message = "Mark is required")
        @DecimalMin(value = "0.0", message = "Mark must be at least 0")
        private BigDecimal mark;

        private boolean sample;
    }
} 