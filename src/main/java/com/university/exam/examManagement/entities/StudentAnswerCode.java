package com.university.exam.examManagement.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "student_answer_code")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentAnswerCode {
    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID studentExamAttemptId;

    @Column(nullable = false)
    private UUID examQuestionId;

    @Column(columnDefinition = "TEXT")
    private String submittedCode;

    private UUID languageId;
    private Double totalScore;
    private String resultSummary;
    private Double aiScore;
} 