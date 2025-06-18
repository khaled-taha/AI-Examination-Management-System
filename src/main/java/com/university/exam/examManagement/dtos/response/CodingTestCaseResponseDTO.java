package com.university.exam.examManagement.dtos.response;

import lombok.Data;
import java.math.BigDecimal;
import java.util.UUID;

@Data
public class CodingTestCaseResponseDTO {
    private UUID id;
    private UUID examQuestionId;
    private String input;
    private String expectedOutput;
    private BigDecimal mark;
    private boolean isSample;
} 