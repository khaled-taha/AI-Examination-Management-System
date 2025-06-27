package com.university.exam.examManagement.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
public class StudentAnswerChoicesResponseDTO {
    private UUID studentExamAttemptId;
    private List<Answer> answers;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Answer {
        private UUID id;
        private UUID examQuestionId;
        private UUID selectedChoiceId;
    }
} 