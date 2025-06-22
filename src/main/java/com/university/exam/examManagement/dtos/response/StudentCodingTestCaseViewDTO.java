package com.university.exam.examManagement.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentCodingTestCaseViewDTO {
    private UUID id;
    private String input;
    private String expectedOutput; // Only populated for sample cases
    private BigDecimal mark;
} 