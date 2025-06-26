package com.university.exam.examManagement.dtos.request;

import lombok.Data;
import jakarta.validation.constraints.*;

import java.util.List;
import java.util.UUID;

@Data
public class ExamQuestionAnswerKeyRequestDTO {

    @NotNull(message = "Exam question ID is required")
    private UUID examQuestionId;
    private List<AnswerKey> answerKeys;

    @Data
    public static class AnswerKey {
        private UUID id;

        @NotBlank(message = "Answer text must not be blank")
        private String answerText;

        private String questionPart;

        private boolean caseSensitive = false;
        private int sortOrder;
    }
}