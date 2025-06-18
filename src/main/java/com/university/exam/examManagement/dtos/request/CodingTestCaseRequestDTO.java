package com.university.exam.examManagement.dtos.request;

import lombok.Data;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.UUID;

@Data
public class CodingTestCaseRequestDTO {
    @NotNull(message = "Exam question ID is required")
    private UUID examQuestionId;

    @NotBlank(message = "Test case input must not be blank")
    private String input;

    @NotBlank(message = "Expected output must not be blank")
    private String expectedOutput;

    @NotNull(message = "Mark is required")
    @DecimalMin(value = "0.0", message = "Mark must be at least 0")
    private BigDecimal mark;

    private boolean isSample = false;
} 