package com.university.codeevaluation.services;

import com.university.codeevaluation.models.CodeExecutionRequest;
import com.university.codeevaluation.models.CodeExecutionResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class JavaCodeEvaluator extends BaseCodeEvaluator {
    
    @Override
    public String getSupportedLanguage() {
        return "java";
    }
    
    @Override
    protected String getFileName(String language) {
        return "Solution.java";
    }
    
    @Override
    protected CodeExecutionResponse.TestCaseResult executeTestCase(Path codeFile, CodeExecutionRequest.TestCase testCase) throws Exception {
        // This method is only called for interpreted languages
        // For Java, compilation and execution are handled in BaseCodeEvaluator
        throw new UnsupportedOperationException("Java requires compilation. Use executeTestCaseWithCompiledExecutable instead.");
    }
    
    /**
     * Handle compilation errors for Java
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
    public boolean isAvailable() {
        // Try multiple Java commands for Windows compatibility
        String[] commands = {
            "java -version",
            "javac -version",
            "java.exe -version",
            "javac.exe -version"
        };
        
        for (String command : commands) {
            try {
                String[] commandArray = command.split("\\s+");
                ProcessBuilder pb = new ProcessBuilder(commandArray);
                
                // Set environment variables for Windows compatibility
                Map<String, String> env = pb.environment();
                if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                    String path = env.get("PATH");
                    String additionalPaths = ";C:\\Program Files\\Java\\jdk-17\\bin;" +
                                           ";C:\\Program Files\\Java\\jdk-11\\bin;" +
                                           ";C:\\Program Files\\Java\\jre-17\\bin;" +
                                           ";C:\\Program Files\\Eclipse Adoptium\\jdk-17.0.9.9-hotspot\\bin;" +
                                           ";C:\\Program Files\\Eclipse Adoptium\\jdk-11.0.21.9-hotspot\\bin";
                    env.put("PATH", path + additionalPaths);
                }
                
                Process process = pb.start();
                boolean completed = process.waitFor(3, TimeUnit.SECONDS);
                
                if (completed && process.exitValue() == 0) {
                    log.info("Java availability check passed with command: {}", command);
                    return true;
                }
            } catch (Exception e) {
                log.debug("Java availability check failed with command '{}': {}", command, e.getMessage());
            }
        }
        
        log.warn("Java availability check failed: No working Java installation found");
        return false;
    }
    
    @Override
    protected String getAvailabilityCheckCommand() {
        return "java -version";
    }
} 