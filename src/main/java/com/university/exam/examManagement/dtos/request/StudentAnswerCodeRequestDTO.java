package com.university.exam.examManagement.dtos.request;

import lombok.Data;
import jakarta.validation.constraints.*;
import java.util.UUID;

@Data
public class StudentAnswerCodeRequestDTO {

    private UUID id;

    @NotNull(message = "Exam question ID is required")
    private UUID examQuestionId;

    @NotBlank(message = "Submitted code must not be blank")
    private String submittedCode;

    @NotNull(message = "Language ID is required")
    private UUID languageId;
} 