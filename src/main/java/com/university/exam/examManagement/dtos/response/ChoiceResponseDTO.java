package com.university.exam.examManagement.dtos.response;

import lombok.Data;
import java.util.UUID;

@Data
public class ChoiceResponseDTO {
    private UUID id;
    private UUID examQuestionId;
    private String choiceText;
    private Boolean isCorrect;
    private double markValue;
} 