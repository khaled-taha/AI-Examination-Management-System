package com.university.exam.examManagement.dtos.response;

import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
public class StudentExamResultResponseDTO {
    private UUID attemptId;
    private UUID examId;
    private String examTitle;
    private double totalExamScore;
    private double totalStudentScore;
    private String attemptStatus; // SUCCESS, FAILED
    private double percentage;
    private List<SectionResultDTO> sectionResults;

    @Data
    public static class SectionResultDTO {
        private UUID sectionId;
        private String sectionTitle;
        private Integer sectionPosition;
        private List<QuestionResultDTO> questionResults;
    }

    @Data
    public static class QuestionResultDTO {
        private UUID questionId;
        private String questionText;
        private String questionType;
        private String explanation;
        private double questionScore;
        private double studentScore;
        private boolean correct;
        private String feedback;
        
        // For Complete questions (questionPart is null)
        private String studentAnswer;
        
        // For Matching questions (questionPart is not null)
        private List<MatchingAnswerDTO> matchingAnswers;
    }

    @Data
    public static class MatchingAnswerDTO {
        private String questionPart;
        private String studentAnswer;
        private int sortOrder;
        private boolean correct;
        private double score;
    }
} 