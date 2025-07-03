package com.university.codeevaluation.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class CodeExecutionRequest {
    
    @NotBlank(message = "Code cannot be empty")
    private String code;
    
    @NotBlank(message = "Language cannot be empty")
    private String language;

    private int time;
    private int memory;
    
    @NotNull(message = "Test cases cannot be null")
    private List<TestCase> testCases;
    
    @Data
    public static class TestCase {
        @NotBlank(message = "Input cannot be empty")
        private String input;
        
        @NotBlank(message = "Expected output cannot be empty")
        private String expectedOutput;
        
        private double mark = 1.0;
        private boolean isSample = false;
    }
} 