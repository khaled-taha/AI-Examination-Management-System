package com.university.exam.examManagement.dtos.request;

import lombok.Data;
import jakarta.validation.constraints.*;
import java.util.UUID;

@Data
public class CopyQuestionFromPoolRequestDTO {
    @NotNull(message = "Question pool ID is required")
    private UUID questionPoolId;

    @NotNull(message = "Section ID is required")
    private UUID sectionId;

    @Min(value = 0, message = "Mark must be at least 0")
    private double mark;

    @Min(value = 1, message = "Question position must be at least 1")
    private Integer position;
} 