package com.university.codeevaluation.services;

import com.university.codeevaluation.models.CodeExecutionRequest;
import com.university.codeevaluation.models.CodeExecutionResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.file.Path;

@Slf4j
@Service
public class CppCodeEvaluator extends BaseCodeEvaluator {
    
    @Override
    public String getSupportedLanguage() {
        return "c++";
    }
    
    @Override
    protected String getFileName(String language) {
        return "solution.cpp";
    }
    
    @Override
    protected CodeExecutionResponse.TestCaseResult executeTestCase(Path codeFile, CodeExecutionRequest.TestCase testCase) throws Exception {
        CodeExecutionResponse.TestCaseResult result = new CodeExecutionResponse.TestCaseResult();
        result.setInput(testCase.getInput());
        result.setExpectedOutput(testCase.getExpectedOutput());
        result.setSample(testCase.isSample());
        
        long startTime = System.currentTimeMillis();
        long startMemory = getMemoryUsageKb();
        
        try {
            // Compile C++ code
            String executableName = "solution";
            ProcessBuilder compilePb = createProcessBuilder(codeFile.getParent(), "g++", "-o", executableName, codeFile.getFileName().toString());
            String compileOutput = executeProcess(compilePb, null);
            
            // Run compiled C++ program
            ProcessBuilder runPb = createProcessBuilder(codeFile.getParent(), "./" + executableName);
            String output = executeProcess(runPb, testCase.getInput());
            
            long endMemory = getMemoryUsageKb();
            long memoryUsed = endMemory - startMemory;
            
            result.setActualOutput(output);
            result.setPassed(output.trim().equals(testCase.getExpectedOutput().trim()));
            result.setMarkObtained(result.isPassed() ? testCase.getMark() : 0.0);
            result.setExecutionTimeMs(System.currentTimeMillis() - startTime);
            result.setMemoryUsedKb(memoryUsed);
            
            if (result.isPassed()) {
                result.setFeedback("Test case passed");
                result.setErrorType("PASSED");
            } else {
                result.setFeedback("Wrong answer: Expected: " + testCase.getExpectedOutput() + ", Got: " + output);
                result.setErrorType("WRONG_ANSWER");
                result.setErrorMessage("Expected: " + testCase.getExpectedOutput() + ", Got: " + output);
            }
            
            // Check memory limit
            checkMemoryLimit(memoryUsed);
            
        } catch (Exception e) {
            long endMemory = getMemoryUsageKb();
            long memoryUsed = endMemory - startMemory;
            
            result.setPassed(false);
            result.setMarkObtained(0.0);
            result.setExecutionTimeMs(System.currentTimeMillis() - startTime);
            result.setMemoryUsedKb(memoryUsed);
            result.setActualOutput("ERROR: " + e.getMessage());
            
            // Categorize the error
            String errorMessage = e.getMessage();
            if (errorMessage != null) {
                if (errorMessage.startsWith("COMPILATION_ERROR:")) {
                    result.setErrorType("COMPILATION_ERROR");
                    result.setErrorMessage(errorMessage.substring("COMPILATION_ERROR:".length()).trim());
                    result.setFeedback("Compilation error: " + result.getErrorMessage());
                } else if (errorMessage.startsWith("RUNTIME_ERROR:")) {
                    result.setErrorType("RUNTIME_ERROR");
                    result.setErrorMessage(errorMessage.substring("RUNTIME_ERROR:".length()).trim());
                    result.setFeedback("Runtime error: " + result.getErrorMessage());
                    result.setStackTrace(errorMessage);
                } else if (errorMessage.startsWith("TIME_LIMIT_EXCEEDED:")) {
                    result.setErrorType("TIME_LIMIT");
                    result.setErrorMessage(errorMessage.substring("TIME_LIMIT_EXCEEDED:".length()).trim());
                    result.setFeedback("Time limit exceeded: " + result.getErrorMessage());
                } else if (errorMessage.startsWith("MEMORY_LIMIT_EXCEEDED:")) {
                    result.setErrorType("MEMORY_LIMIT");
                    result.setErrorMessage(errorMessage.substring("MEMORY_LIMIT_EXCEEDED:".length()).trim());
                    result.setFeedback("Memory limit exceeded: " + result.getErrorMessage());
                } else {
                    result.setErrorType("RUNTIME_ERROR");
                    result.setErrorMessage(errorMessage);
                    result.setFeedback("Runtime error: " + errorMessage);
                    result.setStackTrace(errorMessage);
                }
            } else {
                result.setErrorType("UNKNOWN_ERROR");
                result.setErrorMessage("Unknown error occurred");
                result.setFeedback("Unknown error occurred");
            }
        }
        
        return result;
    }
    
    @Override
    protected String getAvailabilityCheckCommand() {
        return "g++ --version";
    }
} 