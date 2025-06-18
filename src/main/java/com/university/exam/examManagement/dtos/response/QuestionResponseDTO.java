package com.university.exam.examManagement.dtos.response;

import lombok.Data;
import java.util.UUID;

@Data
public class QuestionResponseDTO {
    private UUID id;
    private UUID examId;
    private UUID sectionId;
    private UUID questionPoolId;
    private String questionText;
    private String questionType;
    private String explanation;
    private UUID programmingLanguageId;
    private Integer timeLimit;
    private Integer memoryLimit;
    private double mark;
    private Integer position;
} 