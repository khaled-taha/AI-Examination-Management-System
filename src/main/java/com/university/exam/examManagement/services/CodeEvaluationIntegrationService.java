package com.university.exam.examManagement.services;

import com.university.exam.examManagement.dtos.CodeEvaluationRequestDTO;
import com.university.exam.examManagement.dtos.CodeEvaluationResponseDTO;
import com.university.exam.examManagement.entities.*;
import com.university.exam.examManagement.repos.StudentAnswerChoiceRepository;
import com.university.exam.examManagement.repos.StudentAnswerCodeRepository;
import com.university.exam.examManagement.repos.StudentAnswerTextRepository;
import com.university.exam.examManagement.repos.StudentCodingTestResultRepository;
import com.university.exam.examManagement.repos.StudentExamAttemptRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CodeEvaluationIntegrationService {

    private final RestTemplate codeEvaluationRestTemplate;
    private final String codeEvaluationServiceUrl;
    private final StudentCodingTestResultRepository studentCodingTestResultRepository;
    private final StudentAnswerCodeRepository studentAnswerCodeRepository;

    /**
     * Asynchronously evaluate code and save results
     * @param studentAnswerCode The student's code answer
     * @param testCases The test cases to evaluate against
     * @param programmingLanguage The programming language
     */
    public void evaluateCodeAsync(StudentAnswerCode studentAnswerCode,
                                 ExamQuestion examQuestion,
                                 List<CodingTestCase> testCases, 
                                 ProgrammingLanguage programmingLanguage) {
        try {
            log.info("Starting async code evaluation for student answer: {}", studentAnswerCode.getId());
            
            // Build the evaluation request
            CodeEvaluationRequestDTO request = buildEvaluationRequest(studentAnswerCode, examQuestion, testCases, programmingLanguage);
            
            // Call the code evaluation service
            CodeEvaluationResponseDTO response = callCodeEvaluationService(request);
            
            // Save the results
            saveEvaluationResults(studentAnswerCode, response, testCases);
            
            // Update the student answer code with summary results
            updateStudentAnswerCode(studentAnswerCode, response);
            
            log.info("Completed async code evaluation for student answer: {}", studentAnswerCode.getId());
            
        } catch (Exception e) {
            log.error("Error during async code evaluation for student answer: {}", studentAnswerCode.getId(), e);
            // Save error result
            saveErrorResult(studentAnswerCode, e.getMessage());
        }
    }

    /**
     * Build the evaluation request for the code evaluation service
     */
    private CodeEvaluationRequestDTO buildEvaluationRequest(StudentAnswerCode studentAnswerCode,
                                                           ExamQuestion examQuestion,
                                                           List<CodingTestCase> testCases, 
                                                           ProgrammingLanguage programmingLanguage) {
        
        List<CodeEvaluationRequestDTO.TestCaseDTO> testCaseDTOs = testCases.stream()
                .map(testCase -> CodeEvaluationRequestDTO.TestCaseDTO.builder()
                        .input(testCase.getInput())
                        .expectedOutput(testCase.getExpectedOutput())
                        .mark(testCase.getMark().doubleValue())
                        .isSample(testCase.isSample())
                        .testCaseId(testCase.getId())
                        .build())
                .collect(Collectors.toList());

        return CodeEvaluationRequestDTO.builder()
                .code(studentAnswerCode.getSubmittedCode())
                .language(programmingLanguage.getName().toLowerCase())
                .timeLimit(examQuestion.getTimeLimit())
                .memoryLimit(examQuestion.getMemoryLimit())
                .testCases(testCaseDTOs)
                .build();
    }

    /**
     * Call the code evaluation service
     */
    private CodeEvaluationResponseDTO callCodeEvaluationService(CodeEvaluationRequestDTO request) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<CodeEvaluationRequestDTO> entity = new HttpEntity<>(request, headers);
            
            String url = codeEvaluationServiceUrl + "/code-evaluation/api/v1/code-evaluation/evaluate";
            
            log.info("Calling code evaluation service at: {}", url);
            
            ResponseEntity<CodeEvaluationResponseDTO> response = codeEvaluationRestTemplate.postForEntity(
                    url, entity, CodeEvaluationResponseDTO.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                log.info("Code evaluation service responded successfully");
                return response.getBody();
            } else {
                throw new RuntimeException("Code evaluation service returned error status: " + response.getStatusCode());
            }
            
        } catch (Exception e) {
            log.error("Error calling code evaluation service", e);
            throw new RuntimeException("Failed to call code evaluation service: " + e.getMessage(), e);
        }
    }

    /**
     * Save the evaluation results to the database
     */
    private void saveEvaluationResults(StudentAnswerCode studentAnswerCode, 
                                     CodeEvaluationResponseDTO response, 
                                     List<CodingTestCase> testCases) {
        
        // Delete any existing results for this student answer code (override previous results)
        studentCodingTestResultRepository.deleteByStudentAnswerCodeId(studentAnswerCode.getId());
        
        if (response.getTestCaseResults() != null) {
            List<StudentCodingTestResult> results = response.getTestCaseResults().stream()
                    .map(resultDTO -> {
                        // Find the corresponding test case
                        CodingTestCase testCase = testCases.stream()
                                .filter(tc -> tc.getId().equals(resultDTO.getTestCaseId()))
                                .findFirst()
                                .orElse(null);

                        StudentCodingTestResult result = new StudentCodingTestResult();
                        result.setId(UUID.randomUUID());
                        result.setStudentAnswerCode(studentAnswerCode);
                        result.setTestCase(testCase);
                        result.setPassed(resultDTO.isPassed());
                        result.setMarkObtained(resultDTO.getMarkObtained());
                        result.setExecutionTimeMs((int) resultDTO.getExecutionTimeMs());
                        result.setMemoryUsedKb((int) resultDTO.getMemoryUsedKb());
                        result.setFeedback(resultDTO.getFeedback());
                        
                        return result;
                    })
                    .collect(Collectors.toList());

            studentCodingTestResultRepository.saveAll(results);
            log.info("Saved {} test case results for student answer: {}", results.size(), studentAnswerCode.getId());
        }
    }

    /**
     * Update the student answer code with summary results
     */
    private void updateStudentAnswerCode(StudentAnswerCode studentAnswerCode, CodeEvaluationResponseDTO response) {
        studentAnswerCode.setTotalScore(response.getTotalScore());
        studentAnswerCode.setResultSummary(response.getResultSummary());
        
        // Save the updated student answer code
        studentAnswerCodeRepository.save(studentAnswerCode);
    }

    /**
     * Save error result when evaluation fails
     */
    private void saveErrorResult(StudentAnswerCode studentAnswerCode, String errorMessage) {
        // Delete any existing results
        studentCodingTestResultRepository.deleteByStudentAnswerCodeId(studentAnswerCode.getId());
        
        // Update the student answer code with error information
        studentAnswerCode.setTotalScore(0.0);
        studentAnswerCode.setResultSummary("Evaluation failed: " + errorMessage);
        
        // Save the updated student answer code
        studentAnswerCodeRepository.save(studentAnswerCode);
        
        log.error("Saved error result for student answer: {} - {}", studentAnswerCode.getId(), errorMessage);
    }

    /**
     * Check if the code evaluation service is available
     */
    public boolean isCodeEvaluationServiceAvailable() {
        try {
            String url = codeEvaluationServiceUrl + "/api/v1/code-evaluation/health";
            ResponseEntity<Object> response = codeEvaluationRestTemplate.getForEntity(url, Object.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            log.warn("Code evaluation service is not available: {}", e.getMessage());
            return false;
        }
    }
} 