package com.university.exam.examManagement.dtos.response;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
import java.util.UUID;

@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "questionType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ChoiceQuestionResponseDTO.class, name = "TF"),
        @JsonSubTypes.Type(value = ChoiceQuestionResponseDTO.class, name = "MCQ"),
        @JsonSubTypes.Type(value = ChoiceQuestionResponseDTO.class, name = "MultiChoice"),
        @JsonSubTypes.Type(value = AnswerKeyQuestionResponseDTO.class, name = "Complete"),
        @JsonSubTypes.Type(value = AnswerKeyQuestionResponseDTO.class, name = "Matching"),
        @JsonSubTypes.Type(value = CodingQuestionResponseDTO.class, name = "Coding")
})
public abstract class QuestionResponseDTO {
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


    public static QuestionResponseDTO createQuestionResponse(String questionType) {
        return switch (questionType) {
            case "TF", "MCQ", "MultiChoice" -> new ChoiceQuestionResponseDTO();
            case "Complete", "Matching" -> new AnswerKeyQuestionResponseDTO();
            case "Coding" -> new CodingQuestionResponseDTO();
            default -> throw new IllegalArgumentException("Unsupported questionType: " + questionType);
        };
    }
}