package com.university.codeevaluation.services;

import com.university.codeevaluation.config.CodeEvaluationConfig;
import com.university.codeevaluation.models.CodeExecutionRequest;
import com.university.codeevaluation.models.CodeExecutionResponse;
import com.university.codeevaluation.utils.ProcessManager;
import com.university.codeevaluation.utils.ProcessResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

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
    protected int timeLimit = 1;
    protected int memoryLimit = 512;
    
    @Autowired
    protected CodeEvaluationConfig config;
    
    @Override
    public CodeExecutionResponse evaluate(CodeExecutionRequest request) {
        CodeExecutionResponse response = new CodeExecutionResponse();
        long startTime = System.currentTimeMillis();
        
        // Use request values if provided, otherwise use language-specific defaults
        if (request.getTime() > 0) {
            timeLimit = request.getTime();
        } else {
            timeLimit = config.getTimeLimitForLanguage(request.getLanguage());
        }
        
        if (request.getMemory() > 0) {
            memoryLimit = request.getMemory();
        } else {
            memoryLimit = config.getMemoryLimitForLanguage(request.getLanguage());
        }
        
        log.info("Evaluating {} code with time limit: {}s, memory limit: {}MB", 
            request.getLanguage(), timeLimit, memoryLimit);
        
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
            
            if (requiresCompilation()) {
                // For compiled languages: compile once, then run all test cases
                log.info("Compiling {} code...", request.getLanguage());
                try {
                    Path executablePath = compileCode(codeFile);
                    log.info("Compilation successful, running {} test cases", request.getTestCases().size());
                    
                    for (CodeExecutionRequest.TestCase testCase : request.getTestCases()) {
                        CodeExecutionResponse.TestCaseResult result = executeTestCaseWithCompiledExecutable(executablePath, testCase);
                        results.add(result);
                        totalMemoryUsed += result.getMemoryUsedKb();
                        
                        if (result.isPassed()) {
                            totalScore += result.getMarkObtained();
                            passedCases++;
                        } else {
                            failedCases++;
                        }
                    }
                } catch (Exception e) {
                    // Handle compilation errors
                    String errorMessage = e.getMessage();
                    if (errorMessage != null && errorMessage.startsWith("COMPILATION_ERROR:")) {
                        String compilationError = errorMessage.substring("COMPILATION_ERROR:".length()).trim();
                        log.warn("Compilation failed: {}", compilationError);
                        
                        // Create failed results for all test cases
                        for (CodeExecutionRequest.TestCase testCase : request.getTestCases()) {
                            CodeExecutionResponse.TestCaseResult result = new CodeExecutionResponse.TestCaseResult();
                            result.setInput(testCase.getInput());
                            result.setExpectedOutput(testCase.getExpectedOutput());
                            result.setSample(testCase.isSample());
                            result.setPassed(false);
                            result.setMarkObtained(0.0);
                            result.setExecutionTimeMs(0);
                            result.setMemoryUsedKb(0);
                            result.setActualOutput("ERROR: " + compilationError);
                            result.setErrorType(compilationError);
                            result.setErrorMessage(compilationError);
                            result.setFeedback(compilationError);
                            
                            results.add(result);
                            failedCases++;
                        }
                    } else {
                        // Re-throw other errors
                        throw e;
                    }
                }
            } else {
                // For interpreted languages: execute each test case individually
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
    
    /**
     * Check if this language requires compilation
     */
    protected boolean requiresCompilation() {
        String language = getSupportedLanguage();
        return "java".equals(language) || "c++".equals(language) || "c".equals(language);
    }
    
    /**
     * Compile the code and return the executable path (for compiled languages)
     */
    protected Path compileCode(Path codeFile) throws Exception {
        String language = getSupportedLanguage();
        
        switch (language.toLowerCase()) {
            case "java":
                return compileJavaCode(codeFile);
            case "c++":
                return compileCppCode(codeFile);
            case "c":
                return compileCCode(codeFile);
            default:
                throw new UnsupportedOperationException("Compilation not supported for language: " + language);
        }
    }
    
    private Path compileJavaCode(Path codeFile) throws Exception {
        // Compile Java code
        ProcessBuilder compilePb = createProcessBuilder(codeFile.getParent(), "javac", codeFile.getFileName().toString());
        executeProcess(compilePb, null);
        
        // Return the compiled class file path
        String className = codeFile.getFileName().toString().replace(".java", "");
        return codeFile.getParent().resolve(className + ".class");
    }
    
    private Path compileCppCode(Path codeFile) throws Exception {
        // Compile C++ code
        String executableName = "solution";
        ProcessBuilder compilePb = createProcessBuilder(codeFile.getParent(), "g++", "-o", executableName, codeFile.getFileName().toString());
        executeProcess(compilePb, null);
        
        // Return the compiled executable path
        return codeFile.getParent().resolve(executableName);
    }
    
    private Path compileCCode(Path codeFile) throws Exception {
        // Compile C code
        String executableName = "solution";
        ProcessBuilder compilePb = createProcessBuilder(codeFile.getParent(), "gcc", "-o", executableName, codeFile.getFileName().toString());
        executeProcess(compilePb, null);
        
        // Return the compiled executable path
        return codeFile.getParent().resolve(executableName);
    }
    
    /**
     * Execute a test case using a compiled executable
     */
    protected CodeExecutionResponse.TestCaseResult executeTestCaseWithCompiledExecutable(Path executablePath, CodeExecutionRequest.TestCase testCase) throws Exception {
        CodeExecutionResponse.TestCaseResult result = new CodeExecutionResponse.TestCaseResult();
        result.setInput(testCase.getInput());
        result.setExpectedOutput(testCase.getExpectedOutput());
        result.setSample(testCase.isSample());
        
        long startTime = System.currentTimeMillis();
        long startMemory = getMemoryUsageKb();
        
        try {
            String language = getSupportedLanguage();
            String output;
            
            if ("java".equals(language)) {
                // For Java, run the class file
                String className = executablePath.getFileName().toString().replace(".class", "");
                ProcessBuilder runPb = createProcessBuilder(executablePath.getParent(), "java", className);
                output = executeProcess(runPb, testCase.getInput());
            } else {
                // For C/C++, run the executable directly
                ProcessBuilder runPb = createProcessBuilder(executablePath.getParent(), executablePath.toString());
                output = executeProcess(runPb, testCase.getInput());
            }
            
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
                if (errorMessage.startsWith("RUNTIME_ERROR:")) {
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
    
    protected ProcessBuilder createProcessBuilder(Path workingDir, String... command) {
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.directory(workingDir.toFile());
        pb.redirectErrorStream(true);
        
        // Set environment variables for memory limits based on language
        Map<String, String> env = pb.environment();
        String language = getSupportedLanguage();
        
        switch (language.toLowerCase()) {
            case "java":
                env.put("JAVA_OPTS", "-Xmx" + memoryLimit + "m -Xms64m");
                break;
            case "python":
                // Python doesn't have built-in memory limits, but we can set some environment variables
                env.put("PYTHONMALLOC", "malloc");
                env.put("PYTHONDEVMODE", "1");
                break;
            case "c++":
            case "c":
                // C/C++ will be handled by OS-level limits
                break;
            case "sql":
                // MySQL memory limits
                env.put("MYSQL_OPTS", "--max-connections=1 --max-user-connections=1");
                break;
        }
        
        return pb;
    }
    
    protected String executeProcess(ProcessBuilder pb, String input) throws Exception {
        ProcessResult result = ProcessManager.executeProcess(pb, input, timeLimit, memoryLimit);
        return result.getOutput();
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