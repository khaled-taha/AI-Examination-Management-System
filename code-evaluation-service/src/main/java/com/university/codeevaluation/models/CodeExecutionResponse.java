package com.university.codeevaluation.models;

import lombok.Data;
import java.util.List;

@Data
public class CodeExecutionResponse {
    private boolean success;
    private String message;
    private double totalScore;
    private String resultSummary;
    private List<TestCaseResult> testCaseResults;
    private long executionTimeMs;
    private long memoryUsedKb;
    
    // Enhanced output categorization
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
        PARTIAL_SUCCESS,  // Some cases passed, some failed
        ALL_PASSED        // All test cases passed
    }
    
    @Data
    public static class TestCaseResult {
        private String input;
        private String expectedOutput;
        private String actualOutput;
        private boolean passed;
        private double markObtained;
        private long executionTimeMs;
        private long memoryUsedKb;
        private String feedback;
        private boolean isSample;
        
        // Enhanced error details
        private String errorType; // COMPILATION_ERROR, RUNTIME_ERROR, TIME_LIMIT, MEMORY_LIMIT, WRONG_ANSWER
        private String errorMessage;
        private String stackTrace;
    }
} 