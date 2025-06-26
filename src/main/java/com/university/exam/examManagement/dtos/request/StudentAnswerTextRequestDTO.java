package com.university.exam.examManagement.dtos.request;

import lombok.Data;
import jakarta.validation.constraints.*;

import java.util.List;
import java.util.UUID;

@Data
public class StudentAnswerTextRequestDTO {

    @NotNull(message = "Exam question ID is required")
    private UUID examQuestionId;

    private List<AnswerText> answerTexts;

    @Data
    public static class AnswerText {

        private UUID id;

        private String questionPart;

        @NotBlank(message = "Student answer must not be blank")
        private String studentAnswer;

        private int sortOrder;
    }
} 