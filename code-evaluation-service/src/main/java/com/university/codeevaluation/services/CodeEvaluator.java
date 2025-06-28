package com.university.codeevaluation.services;

import com.university.codeevaluation.models.CodeExecutionRequest;
import com.university.codeevaluation.models.CodeExecutionResponse;

public interface CodeEvaluator {
    
    /**
     * Evaluates code against test cases
     * @param request The code execution request
     * @return The execution response with results
     */
    CodeExecutionResponse evaluate(CodeExecutionRequest request);
    
    /**
     * Gets the programming language this evaluator supports
     * @return The programming language name
     */
    String getSupportedLanguage();
    
    /**
     * Checks if the evaluator is available (compiler/interpreter installed)
     * @return true if available, false otherwise
     */
    boolean isAvailable();
} 