package com.university.exam.examManagement.dtos.response;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
import java.util.UUID;

@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "questionType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ExamQuestionChoiceResponseDTO.class, name = "TF"),
        @JsonSubTypes.Type(value = ExamQuestionChoiceResponseDTO.class, name = "MCQ"),
        @JsonSubTypes.Type(value = ExamQuestionChoiceResponseDTO.class, name = "MultiChoice"),
        @JsonSubTypes.Type(value = ExamQuestionAnswerKeyResponseDTO.class, name = "Complete"),
        @JsonSubTypes.Type(value = ExamQuestionAnswerKeyResponseDTO.class, name = "Matching"),
        @JsonSubTypes.Type(value = ExamQuestionCodingResponseDTO.class, name = "Coding")
})
public abstract class ExamQuestionResponseDTO {
    private UUID id;
    private UUID examId;
    private UUID sectionId;
    private UUID questionPoolId;
    private String questionText;
    private String questionType;
    private String explanation;
    private Integer timeLimit;
    private Integer memoryLimit;
    private double mark;
    private Integer position;
    private boolean active; // for soft-delete and grade.


    public static ExamQuestionResponseDTO createQuestionResponse(String questionType) {
        return switch (questionType) {
            case "TF", "MCQ", "MultiChoice" -> new ExamQuestionChoiceResponseDTO();
            case "Complete", "Matching" -> new ExamQuestionAnswerKeyResponseDTO();
            case "Coding" -> new ExamQuestionCodingResponseDTO();
            default -> throw new IllegalArgumentException("Unsupported questionType: " + questionType);
        };
    }
}