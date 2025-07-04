package com.university.codeevaluation.services;

import com.university.codeevaluation.models.CodeExecutionRequest;
import com.university.codeevaluation.models.CodeExecutionResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
public abstract class BaseCodeEvaluator implements CodeEvaluator {
    
    protected static final String TEMP_DIR = System.getProperty("java.io.tmpdir");
    protected int timeLimit = 10;
    protected int memoryLimit = 512;
    
    @Override
    public CodeExecutionResponse evaluate(CodeExecutionRequest request) {
        CodeExecutionResponse response = new CodeExecutionResponse();
        long startTime = System.currentTimeMillis();
        timeLimit = request.getTime();
        memoryLimit = request.getMemory();

        try {
            // Create temporary directory for this execution
            String executionId = UUID.randomUUID().toString();
            Path tempDir = Paths.get(TEMP_DIR, "code-eval-" + executionId);
            Files.createDirectories(tempDir);
            
            // Write code to file
            String fileName = getFileName(request.getLanguage());
            Path codeFile = tempDir.resolve(fileName);
            Files.write(codeFile, request.getCode().getBytes());
            
            // Execute test cases
            List<CodeExecutionResponse.TestCaseResult> results = new ArrayList<>();
            double totalScore = 0.0;
            long totalMemoryUsed = 0;
            int passedCases = 0;
            int failedCases = 0;
            
            for (CodeExecutionRequest.TestCase testCase : request.getTestCases()) {
                CodeExecutionResponse.TestCaseResult result = executeTestCase(codeFile, testCase);
                results.add(result);
                totalMemoryUsed += result.getMemoryUsedKb();
                
                if (result.isPassed()) {
                    totalScore += result.getMarkObtained();
                    passedCases++;
                } else {
                    failedCases++;
                }
            }
            
            // Clean up
            cleanup(tempDir);
            
            // Determine output type and set response
            CodeExecutionResponse.OutputType outputType = determineOutputType(results, passedCases, failedCases);
            String resultSummary = generateResultSummary(passedCases, failedCases, totalScore, totalMemoryUsed, outputType);
            
            response.setSuccess(outputType == CodeExecutionResponse.OutputType.ALL_PASSED);
            response.setMessage(generateMessage(outputType, passedCases, failedCases));
            response.setTotalScore(totalScore); // Only sum of passed test case marks
            response.setTestCaseResults(results);
            response.setExecutionTimeMs(System.currentTimeMillis() - startTime);
            response.setMemoryUsedKb(totalMemoryUsed);
            response.setOutputType(outputType);
            response.setPassedCases(passedCases);
            response.setFailedCases(failedCases);
            response.setTotalCases(results.size());
            response.setResultSummary(resultSummary);
            
        } catch (Exception e) {
            log.error("Error evaluating code: {}", e.getMessage(), e);
            response.setSuccess(false);
            response.setMessage("Error executing code: " + e.getMessage());
            response.setOutputType(CodeExecutionResponse.OutputType.RUNTIME_ERROR);
            response.setErrorDetails(e.getMessage());
            response.setTotalScore(0.0);
        }
        
        return response;
    }
    
    protected abstract String getFileName(String language);
    
    protected abstract CodeExecutionResponse.TestCaseResult executeTestCase(Path codeFile, CodeExecutionRequest.TestCase testCase) throws Exception;
    
    protected ProcessBuilder createProcessBuilder(Path workingDir, String... command) {
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.directory(workingDir.toFile());
        pb.redirectErrorStream(true);
        
        // Set memory limit for the process
        Map<String, String> env = pb.environment();
        env.put("JAVA_OPTS", "-Xmx" + memoryLimit + "m");
        
        return pb;
    }
    
    protected String executeProcess(ProcessBuilder pb, String input) throws Exception {
        Process process = pb.start();
        
        // Write input to process
        if (input != null && !input.trim().isEmpty()) {
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()))) {
                writer.write(input);
                writer.flush();
            }
        }
        
        // Wait for completion with timeout
        boolean completed = process.waitFor(timeLimit, TimeUnit.SECONDS);
        if (!completed) {
            process.destroyForcibly();
            throw new RuntimeException("TIME_LIMIT_EXCEEDED: Process timed out after " + timeLimit + " seconds");
        }
        
        // Read output
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }
        
        int exitCode = process.exitValue();
        if (exitCode != 0) {
            throw new RuntimeException("RUNTIME_ERROR: Process failed with exit code " + exitCode + ": " + output.toString());
        }
        
        return output.toString().trim();
    }
    
    protected long getMemoryUsageKb() {
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        long usedMemory = memoryBean.getHeapMemoryUsage().getUsed();
        return usedMemory / 1024; // Convert bytes to KB
    }
    
    protected void checkMemoryLimit(long memoryUsedKb) {
        long memoryLimitKb = memoryLimit * 1024;
        if (memoryUsedKb > memoryLimitKb) {
            throw new RuntimeException("MEMORY_LIMIT_EXCEEDED: Memory limit exceeded: " + memoryUsedKb + " KB > " + memoryLimitKb + " KB");
        }
    }
    
    protected CodeExecutionResponse.OutputType determineOutputType(List<CodeExecutionResponse.TestCaseResult> results, int passedCases, int failedCases) {
        if (failedCases == 0) {
            return CodeExecutionResponse.OutputType.ALL_PASSED;
        }
        
        if (passedCases == 0) {
            // Check if all failures are due to the same error type
            String firstErrorType = results.get(0).getErrorType();
            boolean allSameError = results.stream().allMatch(r -> firstErrorType.equals(r.getErrorType()));
            
            if (allSameError) {
                switch (firstErrorType) {
                    case "COMPILATION_ERROR":
                        return CodeExecutionResponse.OutputType.COMPILATION_ERROR;
                    case "RUNTIME_ERROR":
                        return CodeExecutionResponse.OutputType.RUNTIME_ERROR;
                    case "TIME_LIMIT":
                        return CodeExecutionResponse.OutputType.TIME_LIMIT_EXCEEDED;
                    case "MEMORY_LIMIT":
                        return CodeExecutionResponse.OutputType.MEMORY_LIMIT_EXCEEDED;
                    default:
                        return CodeExecutionResponse.OutputType.PARTIAL_SUCCESS;
                }
            }
        }
        
        return CodeExecutionResponse.OutputType.PARTIAL_SUCCESS;
    }
    
    protected String generateMessage(CodeExecutionResponse.OutputType outputType, int passedCases, int failedCases) {
        switch (outputType) {
            case ALL_PASSED:
                return "All test cases passed successfully";
            case COMPILATION_ERROR:
                return "Compilation error occurred";
            case RUNTIME_ERROR:
                return "Runtime error occurred";
            case TIME_LIMIT_EXCEEDED:
                return "Time limit exceeded";
            case MEMORY_LIMIT_EXCEEDED:
                return "Memory limit exceeded";
            case PARTIAL_SUCCESS:
                return String.format("Partial success: %d passed, %d failed", passedCases, failedCases);
            default:
                return "Unknown execution status";
        }
    }
    
    protected String generateResultSummary(int passedCases, int failedCases, double totalScore, long totalMemoryUsed, CodeExecutionResponse.OutputType outputType) {
        String baseSummary = String.format("Passed %d/%d test cases with total score %.2f (Memory: %d KB)", 
            passedCases, passedCases + failedCases, totalScore, totalMemoryUsed);
        
        switch (outputType) {
            case COMPILATION_ERROR:
                return "COMPILATION ERROR: " + baseSummary;
            case RUNTIME_ERROR:
                return "RUNTIME ERROR: " + baseSummary;
            case TIME_LIMIT_EXCEEDED:
                return "TIME LIMIT EXCEEDED: " + baseSummary;
            case MEMORY_LIMIT_EXCEEDED:
                return "MEMORY LIMIT EXCEEDED: " + baseSummary;
            case PARTIAL_SUCCESS:
                return "PARTIAL SUCCESS: " + baseSummary;
            case ALL_PASSED:
                return "ALL PASSED: " + baseSummary;
            default:
                return baseSummary;
        }
    }
    
    protected void cleanup(Path tempDir) {
        try {
            Files.walk(tempDir)
                .sorted((a, b) -> b.compareTo(a)) // Delete files first, then directories
                .forEach(path -> {
                    try {
                        Files.deleteIfExists(path);
                    } catch (IOException e) {
                        log.warn("Could not delete temporary file: {}", path, e);
                    }
                });
        } catch (IOException e) {
            log.warn("Error cleaning up temporary directory: {}", tempDir, e);
        }
    }
    
    @Override
    public boolean isAvailable() {
        try {
            String command = getAvailabilityCheckCommand();
            
            // Split command into array for ProcessBuilder
            String[] commandArray = command.split("\\s+");
            ProcessBuilder pb = new ProcessBuilder(commandArray);
            
            // Set environment variables for Windows compatibility
            Map<String, String> env = pb.environment();
            if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                // Add common Windows paths for compilers/interpreters
                String path = env.get("PATH");
                String additionalPaths = ";C:\\Program Files\\Java\\jdk-17\\bin;" +
                                       ";C:\\Program Files\\Java\\jdk-11\\bin;" +
                                       ";C:\\Program Files\\Java\\jre-17\\bin;" +
                                       ";C:\\Python311\\;" +
                                       ";C:\\Python310\\;" +
                                       ";C:\\Python39\\;" +
                                       ";C:\\MinGW\\bin;" +
                                       ";C:\\Program Files\\MySQL\\MySQL Server 8.0\\bin";
                env.put("PATH", path + additionalPaths);
            }
            
            Process process = pb.start();
            boolean completed = process.waitFor(5, TimeUnit.SECONDS);
            
            if (completed && process.exitValue() == 0) {
                log.info("Availability check passed for {}: {}", getSupportedLanguage(), command);
                return true;
            } else {
                log.warn("Availability check failed for {}: Command '{}' returned exit code {}", 
                    getSupportedLanguage(), command, completed ? process.exitValue() : "timeout");
                return false;
            }
        } catch (Exception e) {
            log.warn("Availability check failed for {}: {}", getSupportedLanguage(), e.getMessage());
            return false;
        }
    }
    
    protected abstract String getAvailabilityCheckCommand();
} 