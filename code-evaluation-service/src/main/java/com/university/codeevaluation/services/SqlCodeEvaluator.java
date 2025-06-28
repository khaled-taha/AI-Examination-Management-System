package com.university.codeevaluation.services;

import com.university.codeevaluation.models.CodeExecutionRequest;
import com.university.codeevaluation.models.CodeExecutionResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class SqlCodeEvaluator extends BaseCodeEvaluator {
    
    @Override
    public String getSupportedLanguage() {
        return "sql";
    }
    
    @Override
    protected String getFileName(String language) {
        return "solution.sql";
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
            // For SQL, we need to handle setup and execution differently
            // The input might contain setup data and the expected output is the expected query result
            String sqlCode = new String(java.nio.file.Files.readAllBytes(codeFile));
            
            // Execute SQL query using mysql command line with proper credentials
            String mysqlHost = System.getenv("MYSQL_HOST");
            String mysqlPort = System.getenv("MYSQL_PORT");
            String mysqlDatabase = System.getenv("MYSQL_DATABASE");
            String mysqlUser = System.getenv("MYSQL_USER");
            String mysqlPassword = System.getenv("MYSQL_PASSWORD");
            
            // Use default values if environment variables are not set
            mysqlHost = mysqlHost != null ? mysqlHost : "localhost";
            mysqlPort = mysqlPort != null ? mysqlPort : "3306";
            mysqlDatabase = mysqlDatabase != null ? mysqlDatabase : "testdb";
            mysqlUser = mysqlUser != null ? mysqlUser : "root";
            mysqlPassword = mysqlPassword != null ? mysqlPassword : "password";
            
            ProcessBuilder runPb = createProcessBuilder(codeFile.getParent(), 
                "mysql", "-h", mysqlHost, "-P", mysqlPort, "-u", mysqlUser, 
                "-p" + mysqlPassword, mysqlDatabase, "-e", sqlCode);
            String output = executeProcess(runPb, testCase.getInput());
            
            // Clean up the output (remove headers and formatting)
            String cleanOutput = cleanSqlOutput(output);
            
            long endMemory = getMemoryUsageKb();
            long memoryUsed = endMemory - startMemory;
            
            result.setActualOutput(cleanOutput);
            
            // Normalize output for comparison (sort rows to handle unordered results)
            String normalizedActual = normalizeSqlOutput(cleanOutput);
            String normalizedExpected = normalizeSqlOutput(testCase.getExpectedOutput());
            
            result.setPassed(normalizedActual.equals(normalizedExpected));
            result.setMarkObtained(result.isPassed() ? testCase.getMark() : 0.0);
            result.setExecutionTimeMs(System.currentTimeMillis() - startTime);
            result.setMemoryUsedKb(memoryUsed);
            
            if (result.isPassed()) {
                result.setFeedback("Query executed successfully");
                result.setErrorType("PASSED");
            } else {
                result.setFeedback("Wrong answer: Expected: " + testCase.getExpectedOutput() + ", Got: " + cleanOutput);
                result.setErrorType("WRONG_ANSWER");
                result.setErrorMessage("Expected: " + testCase.getExpectedOutput() + ", Got: " + cleanOutput);
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
                    result.setFeedback("SQL syntax error: " + result.getErrorMessage());
                } else if (errorMessage.startsWith("RUNTIME_ERROR:")) {
                    result.setErrorType("RUNTIME_ERROR");
                    result.setErrorMessage(errorMessage.substring("RUNTIME_ERROR:".length()).trim());
                    result.setFeedback("SQL execution error: " + result.getErrorMessage());
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
                    result.setFeedback("SQL execution error: " + errorMessage);
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
    
    private String cleanSqlOutput(String output) {
        // Remove MySQL formatting and headers
        String[] lines = output.split("\n");
        StringBuilder cleanOutput = new StringBuilder();
        
        boolean skipHeader = true;
        for (String line : lines) {
            // Skip MySQL warning messages (various formats)
            if (line.startsWith("mysql: [Warning]") || 
                line.contains("Using a password on the command line interface can be insecure") ||
                line.startsWith("Warning:") ||
                line.contains("Warning:")) {
                continue;
            }
            
            // Skip formatting lines
            if (line.startsWith("+") || line.startsWith("|") || line.contains("---")) {
                continue;
            }
            
            // Skip empty lines
            if (line.trim().isEmpty()) {
                continue;
            }
            
            // Skip header row that contains field information (but keep column names)
            if (skipHeader && (line.contains("Field") || line.contains("Type") || line.contains("Null") || line.contains("Key"))) {
                continue;
            }
            
            // Convert tab-separated values to newline-separated format
            String processedLine = line.trim();
            if (processedLine.contains("\t")) {
                // Split by tabs and join with newlines
                String[] columns = processedLine.split("\t");
                for (int i = 0; i < columns.length; i++) {
                    cleanOutput.append(columns[i].trim());
                    if (i < columns.length - 1) {
                        cleanOutput.append("\n");
                    }
                }
            } else {
                // Single column or already newline-separated
                cleanOutput.append(processedLine);
            }
            cleanOutput.append("\n");
        }
        
        return cleanOutput.toString().trim();
    }
    
    private String normalizeSqlOutput(String output) {
        String[] lines = output.split("\n");
        if (lines.length <= 2) {
            // Single row result, no need to sort
            return output.trim();
        }
        
        // Separate headers from data rows
        String[] headers = new String[2];
        List<String[]> dataRows = new ArrayList<>();
        
        // Parse the output structure
        int currentIndex = 0;
        for (String line : lines) {
            if (currentIndex < 2) {
                headers[currentIndex] = line.trim();
                currentIndex++;
            } else {
                // Data rows come in pairs (major, count)
                if (currentIndex % 2 == 0) {
                    // This is a major name
                    String major = line.trim();
                    String count = "";
                    if (currentIndex + 1 < lines.length) {
                        count = lines[currentIndex + 1].trim();
                    }
                    dataRows.add(new String[]{major, count});
                }
                currentIndex++;
            }
        }
        
        // Sort data rows by major name
        dataRows.sort((a, b) -> a[0].compareTo(b[0]));
        
        // Reconstruct the output
        StringBuilder normalized = new StringBuilder();
        normalized.append(headers[0]).append("\n");
        normalized.append(headers[1]).append("\n");
        
        for (String[] row : dataRows) {
            normalized.append(row[0]).append("\n");
            normalized.append(row[1]).append("\n");
        }
        
        return normalized.toString().trim();
    }
    
    @Override
    protected String getAvailabilityCheckCommand() {
        return "mysql --version";
    }
} 