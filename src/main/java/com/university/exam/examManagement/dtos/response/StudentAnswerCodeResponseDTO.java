package com.university.exam.examManagement.dtos.response;

import lombok.Data;
import java.util.UUID;

@Data
public class StudentAnswerCodeResponseDTO {
    private UUID id;
    private UUID studentExamAttemptId;
    private UUID examQuestionId;
    private String submittedCode;
    private UUID languageId;
}