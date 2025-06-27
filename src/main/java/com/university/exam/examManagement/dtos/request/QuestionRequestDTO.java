package com.university.exam.examManagement.dtos.request;

import lombok.Data;
import jakarta.validation.constraints.*;
import java.util.UUID;

@Data
public class QuestionRequestDTO {

    private UUID id;

    @NotNull(message = "Exam ID is required")
    private UUID examId;

    @NotNull(message = "Section ID is required")
    private UUID sectionId;

    private UUID questionPoolId;

    @NotBlank(message = "Question text must not be blank")
    private String questionText;

    @NotBlank(message = "Question type must not be blank")
    private String questionType;

    private String explanation;

    private UUID programmingLanguageId;

    @Min(value = 1, message = "Time limit must be at least 1 second")
    private Integer timeLimit;

    @Min(value = 1, message = "Memory limit must be at least 1 KB")
    private Integer memoryLimit;

    @Min(value = 0, message = "Mark must be at least 0")
    private double mark;

    @Min(value = 1, message = "Question position must be at least 1")
    private Integer position;

    private boolean active; // for soft-delete and grade.
} 