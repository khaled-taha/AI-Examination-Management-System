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
@Table(name = "question_pool")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionPool {
    @Id
    private UUID id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String questionText;

    @Column(nullable = false, length = 50)
    private String questionType;

    @Column(columnDefinition = "TEXT")
    private String explanation;

    private UUID programmingLanguageId;
    private Integer timeLimit;
    private Integer memoryLimit;
    private UUID creatorId;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
} 