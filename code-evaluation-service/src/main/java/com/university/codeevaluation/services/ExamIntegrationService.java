package com.university.codeevaluation.services;

import com.university.codeevaluation.models.CodeExecutionRequest;
import com.university.codeevaluation.models.CodeExecutionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

/**
 * Example integration service showing how the main exam management system
 * can integrate with this code evaluation microservice.
 * 
 * This service demonstrates:
 * 1. How to call the code evaluation service
 * 2. How to handle responses
 * 3. How to map exam data to evaluation requests
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExamIntegrationService {
    
    private final RestTemplate restTemplate;
    private final String codeEvaluationBaseUrl = "http://localhost:8081/code-evaluation/api/v1/code-evaluation";
    
    /**
     * Example method to evaluate a student's code submission
     * This would be called from the main exam management system
     */
    public CodeExecutionResponse evaluateStudentCodeSubmission(
            String studentCode, 
            String language, 
            List<Map<String, Object>> testCases) {
        
        try {
            // Convert exam test cases to evaluation format
            List<CodeExecutionRequest.TestCase> evaluationTestCases = testCases.stream()
                .map(this::convertToEvaluationTestCase)
                .toList();
            
            // Create evaluation request
            CodeExecutionRequest request = new CodeExecutionRequest();
            request.setCode(studentCode);
            request.setLanguage(language);
            request.setTestCases(evaluationTestCases);
            
            // Call the code evaluation service
            log.info("Evaluating code for language: {}", language);
            CodeExecutionResponse response = restTemplate.postForObject(
                codeEvaluationBaseUrl + "/evaluate", 
                request, 
                CodeExecutionResponse.class
            );
            
            if (response != null) {
                log.info("Code evaluation completed. Success: {}, Score: {}", 
                    response.isSuccess(), response.getTotalScore());
            }
            
            return response;
            
        } catch (Exception e) {
            log.error("Error evaluating code: {}", e.getMessage(), e);
            
            // Return error response
            CodeExecutionResponse errorResponse = new CodeExecutionResponse();
            errorResponse.setSuccess(false);
            errorResponse.setMessage("Evaluation service error: " + e.getMessage());
            return errorResponse;
        }
    }
    
    /**
     * Check if the code evaluation service is available
     */
    public boolean isEvaluationServiceAvailable() {
        try {
            Map<String, Object> health = restTemplate.getForObject(
                codeEvaluationBaseUrl + "/health", 
                Map.class
            );
            return health != null && "UP".equals(health.get("status"));
        } catch (Exception e) {
            log.warn("Code evaluation service not available: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Get supported languages from the evaluation service
     */
    public List<String> getSupportedLanguages() {
        try {
            return restTemplate.getForObject(
                codeEvaluationBaseUrl + "/languages", 
                List.class
            );
        } catch (Exception e) {
            log.error("Error getting supported languages: {}", e.getMessage());
            return List.of();
        }
    }
    
    /**
     * Convert exam test case format to evaluation service format
     */
    private CodeExecutionRequest.TestCase convertToEvaluationTestCase(Map<String, Object> examTestCase) {
        CodeExecutionRequest.TestCase testCase = new CodeExecutionRequest.TestCase();
        testCase.setInput((String) examTestCase.get("input"));
        testCase.setExpectedOutput((String) examTestCase.get("expectedOutput"));
        testCase.setMark(((Number) examTestCase.get("mark")).doubleValue());
        testCase.setSample((Boolean) examTestCase.getOrDefault("isSample", false));
        return testCase;
    }
    
    /**
     * Example method showing how to process evaluation results
     * and update the exam database
     */
    public void processEvaluationResults(String studentAttemptId, CodeExecutionResponse response) {
        if (response.isSuccess()) {
            // Update student answer with results
            log.info("Processing successful evaluation for attempt: {}", studentAttemptId);
            log.info("Total score: {}, Test cases passed: {}/{}", 
                response.getTotalScore(),
                response.getTestCaseResults().stream().mapToInt(r -> r.isPassed() ? 1 : 0).sum(),
                response.getTestCaseResults().size()
            );
            
            // Here you would:
            // 1. Update student_answer_code table with total_score and result_summary
            // 2. Insert records into student_coding_test_result table for each test case
            // 3. Update the overall exam attempt status if needed
            
        } else {
            log.error("Evaluation failed for attempt: {}. Error: {}", 
                studentAttemptId, response.getMessage());
            
            // Handle evaluation failure
            // Update student_answer_code with error information
        }
    }
} 