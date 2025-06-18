package com.university.exam.examManagement.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "student_exam_attempt")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentExamAttempt {
    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID studentId;

    @Column(nullable = false)
    private UUID examId;

    @Column(nullable = false)
    private int attemptNumber;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Double score;
    private String status;
} 