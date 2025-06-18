package com.university.exam.examManagement.dtos.response;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class StudentAttemptResponseDTO {
    private UUID id;
    private UUID studentId;
    private UUID examId;
    private int attemptNumber;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Double score;
    private String status;
} 