package com.university.exam.examManagement.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class StudentAnswerTextResponseDTO {
    private UUID studentExamAttemptId;
    private UUID examQuestionId;
    public List<AnswerText> answerTexts;

    @Data
    @AllArgsConstructor
    public static class AnswerText {
        private UUID id;
        private String questionPart;
        private String studentAnswer;
        private int sortOrder;


    }
}