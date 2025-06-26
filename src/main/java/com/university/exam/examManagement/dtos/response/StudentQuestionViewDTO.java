package com.university.exam.examManagement.dtos.response;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
//@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "questionType")
//@JsonSubTypes({
//        @JsonSubTypes.Type(value = StudentChoiceQuestionViewDTO.class, name = "TF"),
//        @JsonSubTypes.Type(value = StudentChoiceQuestionViewDTO.class, name = "MCQ"),
//        @JsonSubTypes.Type(value = StudentChoiceQuestionViewDTO.class, name = "MultiChoice"),
//        @JsonSubTypes.Type(value = StudentAnswerKeyQuestionViewDTO.class, name = "Complete"),
//        @JsonSubTypes.Type(value = StudentAnswerKeyQuestionViewDTO.class, name = "Matching"),
//        @JsonSubTypes.Type(value = StudentCodingQuestionViewDTO.class, name = "Coding")
//})
public abstract class StudentQuestionViewDTO {
    private UUID id;
    private String questionText;
    private String questionType;
    private String explanation;
    private double mark;
    private Integer position;
    private Integer timeLimit;
    private Integer memoryLimit;
    private boolean active; // for soft-delete and grade.

    public static StudentQuestionViewDTO createStudentQuestionView(String questionType) {
        return switch (questionType) {
            case "TF", "MCQ", "MultiChoice" -> new StudentChoiceQuestionViewDTO();
            case "Complete", "Matching" -> new StudentAnswerKeyQuestionViewDTO();
            case "Coding" -> new StudentCodingQuestionViewDTO();
            default -> throw new IllegalArgumentException("Unsupported questionType: " + questionType);
        };
    }
}