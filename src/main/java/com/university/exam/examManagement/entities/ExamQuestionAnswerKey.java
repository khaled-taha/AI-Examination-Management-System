package com.university.exam.examManagement.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Stores correct answers for non-choice questions.
 */
@Entity
@Table(name = "exam_question_answer_key")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExamQuestionAnswerKey {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_question_id")
    private ExamQuestion examQuestion;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String answerText;

    @Column(columnDefinition = "TEXT")
    private String questionPart;

    @Column(nullable = false)
    private boolean caseSensitive = false;

    @Column(nullable = false)
    private int sortOrder = 1;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
} 