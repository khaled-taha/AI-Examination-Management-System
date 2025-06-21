package com.university.exam.examManagement.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Stores student answers for complete/matching.
 */
@Entity
@Table(name = "student_answer_text")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentAnswerText {
    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_exam_attempt_id", nullable = false)
    private StudentExamAttempt studentExamAttempt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_question_id", nullable = false)
    private ExamQuestion examQuestion;

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