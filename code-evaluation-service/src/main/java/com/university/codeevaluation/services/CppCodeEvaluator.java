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
        // This method is only called for interpreted languages
        // For C++, compilation and execution are handled in BaseCodeEvaluator
        throw new UnsupportedOperationException("C++ requires compilation. Use executeTestCaseWithCompiledExecutable instead.");
    }
    
    /**
     * Handle compilation errors for C++
     */
    @Override
    protected Path compileCode(Path codeFile) throws Exception {
        try {
            return super.compileCode(codeFile);
        } catch (Exception e) {
            // Wrap compilation errors with proper error type
            throw new RuntimeException("COMPILATION_ERROR: " + e.getMessage());
        }
    }
    
    @Override
    protected String getAvailabilityCheckCommand() {
        return "g++ --version";
    }
} 