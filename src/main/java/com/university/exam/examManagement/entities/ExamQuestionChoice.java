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
@Table(name = "exam_question_choice")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExamQuestionChoice {
    @Id
    private UUID id;

    private UUID examQuestionId;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String choiceText;

    private Boolean isCorrect;

    @Column(nullable = false)
    private double markValue;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
} 