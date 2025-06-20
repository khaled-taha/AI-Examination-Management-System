package com.university.exam.examManagement.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "coding_test_case")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CodingTestCase {
    @Id
    private UUID id;

    private UUID examQuestionId;

    @Column(columnDefinition = "TEXT")
    private String input;

    @Column(columnDefinition = "TEXT")
    private String expectedOutput;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal mark;

    @Column(nullable = false)
    private boolean isSample = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
} 