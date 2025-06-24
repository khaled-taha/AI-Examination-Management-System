package com.university.exam.examManagement.dtos.response;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class ExamQuestionChoicesResponseDTO {
    private UUID examQuestionId;
    private List<Choice> choices;

    @Data
    public static class Choice {
        private UUID id;
        private String choiceText;
        private Boolean isCorrect;
        private double markValue;
    }
} 