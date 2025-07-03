package com.university.exam.examManagement.controllers;

import com.university.exam.examManagement.services.CodeEvaluationIntegrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/code-evaluation")
@Tag(name = "Code Evaluation Integration", description = "APIs for code evaluation integration status and health")
@RequiredArgsConstructor
public class CodeEvaluationController {

    private final CodeEvaluationIntegrationService codeEvaluationIntegrationService;

    @GetMapping("/health")
    @Operation(summary = "Check code evaluation service health", description = "Returns the health status of the code evaluation service integration")
    public ResponseEntity<Map<String, Object>> checkHealth() {
        boolean isAvailable = codeEvaluationIntegrationService.isCodeEvaluationServiceAvailable();
        
        Map<String, Object> health = Map.of(
            "status", isAvailable ? "UP" : "DOWN",
            "service", "Code Evaluation Integration",
            "codeEvaluationServiceAvailable", isAvailable,
            "message", isAvailable ? "Code evaluation service is available" : "Code evaluation service is not available"
        );
        
        return ResponseEntity.ok(health);
    }

    @GetMapping("/status")
    @Operation(summary = "Get integration status", description = "Returns detailed status information about the code evaluation integration")
    public ResponseEntity<Map<String, Object>> getStatus() {
        boolean isAvailable = codeEvaluationIntegrationService.isCodeEvaluationServiceAvailable();
        
        Map<String, Object> status = Map.of(
            "integrationEnabled", true,
            "codeEvaluationServiceAvailable", isAvailable,
            "asyncProcessingEnabled", true,
            "status", isAvailable ? "OPERATIONAL" : "SERVICE_UNAVAILABLE"
        );
        
        return ResponseEntity.ok(status);
    }
} 