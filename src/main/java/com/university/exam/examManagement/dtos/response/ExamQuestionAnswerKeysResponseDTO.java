package com.university.exam.examManagement.dtos.response;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class ExamQuestionAnswerKeysResponseDTO {
    private UUID examQuestionId;
    private List<AnswerKey> answerKeys;

    @Data
    public static class AnswerKey {
        private UUID id;
        private String answerText;
        private String questionPart;
        private boolean caseSensitive;
        private int sortOrder;
    }
}