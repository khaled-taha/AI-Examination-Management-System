package com.university.exam.examManagement.dtos.response;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class StudentAnswerTextResponseDTO {
    private UUID id;
    private UUID studentExamAttemptId;
    private UUID examQuestionId;
    private String questionPart;
    private String studentAnswer;
    private LocalDateTime submittedAt;
    private Boolean isCorrect;
    private Double markObtained;
    private Double similarityScore;
} 