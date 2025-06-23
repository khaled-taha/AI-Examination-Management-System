package com.university.exam.examManagement.dtos.request;

import lombok.Data;
import jakarta.validation.constraints.*;
import java.util.UUID;

@Data
public class ChoiceRequestDTO {

    private UUID id;

    @NotNull(message = "Exam question ID is required")
    private UUID examQuestionId;

    @NotBlank(message = "Choice text must not be blank")
    private String choiceText;

    private Boolean isCorrect;

    @Min(value = 0, message = "Mark value must be at least 0")
    private double markValue;
} 