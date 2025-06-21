package com.university.exam.examManagement.dtos.response;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CanEnterExamResponseDTO {
    private boolean canEnter;
    private boolean shouldAttempt;
    private String message;
    private String reason;
} 