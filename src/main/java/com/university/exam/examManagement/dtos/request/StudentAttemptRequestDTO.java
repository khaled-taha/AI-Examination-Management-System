package com.university.exam.examManagement.dtos.request;

import lombok.Data;
import jakarta.validation.constraints.*;
import java.util.UUID;

@Data
public class StudentAttemptRequestDTO {
    @NotNull(message = "Student ID is required")
    private UUID studentId;

    @NotNull(message = "Exam ID is required")
    private UUID examId;

    @Min(value = 1, message = "Attempt number must be at least 1")
    private int attemptNumber;
} 