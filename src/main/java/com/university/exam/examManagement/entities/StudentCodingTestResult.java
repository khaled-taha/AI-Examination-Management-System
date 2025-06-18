package com.university.exam.examManagement.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "student_coding_test_result")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentCodingTestResult {
    @Id
    private UUID id;

    private UUID studentAnswerCodeId;
    private UUID testCaseId;
    private Boolean passed;
    private Double markObtained;
    private Integer executionTimeMs;
    private Integer memoryUsedKb;
    private String feedback;
} 