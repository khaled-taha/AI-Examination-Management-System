package com.university.exam.examManagement.dtos.request;

import lombok.Data;
import jakarta.validation.constraints.*;
import java.util.UUID;

@Data
public class AnswerKeyRequestDTO {
    @NotNull(message = "Exam question ID is required")
    private UUID examQuestionId;

    @NotBlank(message = "Answer text must not be blank")
    private String answerText;

    private String questionPart;

    private boolean caseSensitive = false;
    private int sortOrder = 1;
}