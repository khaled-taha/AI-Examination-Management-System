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
@Table(name = "exam")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Exam {
    @Id
    private UUID id;

    @Column(nullable = false, length = 255)
    private String title;

    private String description;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime endDate;

    @Column(length = 50)
    private String status;

    private UUID creatorId;
    private String courseCode;
    private UUID termId;
    private UUID academicYearGroupId;

    @Column(nullable = false)
    private double successPercentage;

    @Column(nullable = false)
    private int allowedAttemptTimes = 1;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
} 