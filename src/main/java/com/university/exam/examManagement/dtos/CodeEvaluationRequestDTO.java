package com.university.exam.examManagement.dtos;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CodeEvaluationRequestDTO {
    
    private String code;
    private String language;
    private int timeLimit;
    private int memoryLimit;
    private List<TestCaseDTO> testCases;
    
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TestCaseDTO {
        private String input;
        private String expectedOutput;
        private double mark;
        private boolean isSample;
        private UUID testCaseId; // To map back to the original test case
    }
} 