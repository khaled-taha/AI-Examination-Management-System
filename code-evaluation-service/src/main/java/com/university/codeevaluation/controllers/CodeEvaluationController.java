package com.university.codeevaluation.controllers;

import com.university.codeevaluation.models.CodeExecutionRequest;
import com.university.codeevaluation.models.CodeExecutionResponse;
import com.university.codeevaluation.services.CodeEvaluationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/code-evaluation")
@Tag(name = "Code Evaluation", description = "APIs for evaluating code in different programming languages")
public class CodeEvaluationController {
    
    private final CodeEvaluationService codeEvaluationService;

    public CodeEvaluationController(CodeEvaluationService codeEvaluationService) {
        this.codeEvaluationService = codeEvaluationService;
    }

    @PostMapping("/evaluate")
    @Operation(summary = "Evaluate code", description = "Evaluates code against test cases for the specified programming language")
    public ResponseEntity<CodeExecutionResponse> evaluateCode(@Valid @RequestBody CodeExecutionRequest request) {
        log.info("Received code evaluation request for language: {}", request.getLanguage());
        CodeExecutionResponse response = codeEvaluationService.evaluateCode(request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/languages")
    @Operation(summary = "Get supported languages", description = "Returns the list of supported programming languages")
    public ResponseEntity<List<String>> getSupportedLanguages() {
        List<String> languages = codeEvaluationService.getSupportedLanguages();
        return ResponseEntity.ok(languages);
    }
    
    @GetMapping("/status")
    @Operation(summary = "Get evaluator status", description = "Returns the availability status of all language evaluators")
    public ResponseEntity<Map<String, Boolean>> getEvaluatorStatus() {
        Map<String, Boolean> status = codeEvaluationService.getEvaluatorStatus();
        return ResponseEntity.ok(status);
    }
    
    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Returns the health status of the code evaluation service")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> health = Map.of(
            "status", "UP",
            "service", "Code Evaluation Service",
            "supportedLanguages", codeEvaluationService.getSupportedLanguages(),
            "evaluatorStatus", codeEvaluationService.getEvaluatorStatus()
        );
        return ResponseEntity.ok(health);
    }
} 