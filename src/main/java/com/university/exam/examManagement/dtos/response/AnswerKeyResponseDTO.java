package com.university.exam.examManagement.dtos.response;

import lombok.Data;
import java.util.UUID;

@Data
public class AnswerKeyResponseDTO {
    private UUID id;
    private UUID examQuestionId;
    private String answerText;
    private String questionPart;
    private boolean caseSensitive;
    private boolean acceptable;
    private int sortOrder;
} 