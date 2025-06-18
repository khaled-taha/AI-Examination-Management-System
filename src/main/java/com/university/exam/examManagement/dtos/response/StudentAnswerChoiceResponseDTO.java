package com.university.exam.examManagement.dtos.response;

import lombok.Data;
import java.util.UUID;

@Data
public class StudentAnswerChoiceResponseDTO {
    private UUID id;
    private UUID studentExamAttemptId;
    private UUID examQuestionId;
    private UUID selectedChoiceId;
} 