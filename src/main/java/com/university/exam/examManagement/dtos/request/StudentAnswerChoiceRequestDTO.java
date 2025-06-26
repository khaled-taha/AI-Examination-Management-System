package com.university.exam.examManagement.dtos.request;

import lombok.Data;
import jakarta.validation.constraints.*;
import java.util.UUID;

@Data
public class StudentAnswerChoiceRequestDTO {

    private UUID id;

    @NotNull(message = "Exam question ID is required")
    private UUID examQuestionId;

    @NotNull(message = "Selected choice ID is required")
    private UUID selectedChoiceId;
} 