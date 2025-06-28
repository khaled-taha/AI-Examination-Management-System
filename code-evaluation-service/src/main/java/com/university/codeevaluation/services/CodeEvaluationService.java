package com.university.codeevaluation.services;

import com.university.codeevaluation.models.CodeExecutionRequest;
import com.university.codeevaluation.models.CodeExecutionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CodeEvaluationService {
    
    private final List<CodeEvaluator> codeEvaluators;
    private final Map<String, CodeEvaluator> evaluatorMap;

    public CodeEvaluationService(List<CodeEvaluator> codeEvaluators) {
        this.codeEvaluators = codeEvaluators;
        this.evaluatorMap = codeEvaluators.stream()
            .collect(Collectors.toMap(
                CodeEvaluator::getSupportedLanguage,
                Function.identity()
            ));
    }
    
    /**
     * Evaluates code for the specified programming language
     * @param request The code execution request
     * @return The execution response
     */
    public CodeExecutionResponse evaluateCode(CodeExecutionRequest request) {
        String language = request.getLanguage().toLowerCase();
        
        CodeEvaluator evaluator = evaluatorMap.get(language);
        if (evaluator == null) {
            CodeExecutionResponse response = new CodeExecutionResponse();
            response.setSuccess(false);
            response.setMessage("Unsupported programming language: " + language);
            return response;
        }
        
        if (!evaluator.isAvailable()) {
            CodeExecutionResponse response = new CodeExecutionResponse();
            response.setSuccess(false);
            response.setMessage("Language evaluator not available: " + language);
            return response;
        }
        
        log.info("Evaluating {} code with {} test cases", language, request.getTestCases().size());
        return evaluator.evaluate(request);
    }
    
    /**
     * Gets the list of supported programming languages
     * @return List of supported languages
     */
    public List<String> getSupportedLanguages() {
        return codeEvaluators.stream()
            .map(CodeEvaluator::getSupportedLanguage)
            .collect(Collectors.toList());
    }
    
    /**
     * Gets the availability status of all evaluators
     * @return Map of language to availability status
     */
    public Map<String, Boolean> getEvaluatorStatus() {
        return codeEvaluators.stream()
            .collect(Collectors.toMap(
                CodeEvaluator::getSupportedLanguage,
                CodeEvaluator::isAvailable
            ));
    }
} 