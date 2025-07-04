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
public class CodeEvaluationResponseDTO {
    
    private boolean success;
    private String message;
    private double totalScore;
    private String resultSummary;
    private List<TestCaseResultDTO> testCaseResults;
    private long executionTimeMs;
    private long memoryUsedKb;
    private OutputType outputType;
    private String errorDetails;
    private int passedCases;
    private int failedCases;
    private int totalCases;
    
    public enum OutputType {
        COMPILATION_ERROR,
        RUNTIME_ERROR,
        TIME_LIMIT_EXCEEDED,
        MEMORY_LIMIT_EXCEEDED,
        PARTIAL_SUCCESS,
        ALL_PASSED
    }
    
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TestCaseResultDTO {
        private String input;
        private String expectedOutput;
        private String actualOutput;
        private boolean passed;
        private double markObtained;
        private long executionTimeMs;
        private long memoryUsedKb;
        private String feedback;
        private boolean isSample;
        private String errorType;
        private String errorMessage;
        private String stackTrace;
        private UUID testCaseId; // To map back to the original test case
    }
} 