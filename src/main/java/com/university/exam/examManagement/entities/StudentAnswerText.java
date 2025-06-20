package com.university.exam.examManagement.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "student_answer_text")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentAnswerText {
    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID studentExamAttemptId;

    @Column(nullable = false)
    private UUID examQuestionId;

    @Column(columnDefinition = "TEXT")
    private String questionPart;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String studentAnswer;

    private LocalDateTime submittedAt;
    private Boolean isCorrect;
    private Double markObtained;
    private Double similarityScore;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
} 