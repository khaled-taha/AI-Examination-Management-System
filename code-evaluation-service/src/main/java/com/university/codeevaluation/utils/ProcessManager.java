package com.university.codeevaluation.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ProcessManager {
    
    /**
     * Execute a process with time and memory limits
     */
    public static ProcessResult executeProcess(ProcessBuilder pb, String input, int timeLimitSeconds, int memoryLimitMb) throws Exception {
        Process process = pb.start();
        
        log.info("Started process with PID: {}", process.pid());
        log.info("Process command: {}", String.join(" ", pb.command()));
        
        // Start memory monitoring thread (only if memory limit is reasonable)
        ProcessMemoryMonitor memoryMonitor = null;
        if (memoryLimitMb > 0 && memoryLimitMb < 10000) { // Skip if limit is too high or disabled
            memoryMonitor = new ProcessMemoryMonitor(process, memoryLimitMb);
            memoryMonitor.start();
        } else {
            log.info("Memory monitoring disabled (limit: {} MB)", memoryLimitMb);
        }
        
        try {
            // Write input to process
            if (input != null && !input.trim().isEmpty()) {
                try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()))) {
                    writer.write(input);
                    writer.flush();
                }
            }
            
            // Wait for completion with timeout
            boolean completed = process.waitFor(timeLimitSeconds, TimeUnit.SECONDS);
            if (!completed) {
                process.destroyForcibly();
                if (memoryMonitor != null) {
                    memoryMonitor.stop();
                }
                throw new RuntimeException("TIME_LIMIT_EXCEEDED: Process timed out after " + timeLimitSeconds + " seconds");
            }
            
            // Stop memory monitoring
            if (memoryMonitor != null) {
                memoryMonitor.stop();
                
                // Check if memory limit was exceeded
                if (memoryMonitor.isMemoryLimitExceeded()) {
                    throw new RuntimeException("MEMORY_LIMIT_EXCEEDED: Memory limit exceeded: " + 
                        memoryMonitor.getMaxMemoryUsed() + " KB > " + (memoryLimitMb * 1024) + " KB");
                }
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
            
            long memoryUsed = (memoryMonitor != null) ? memoryMonitor.getMaxMemoryUsed() : 0;
            return new ProcessResult(output.toString().trim(), memoryUsed, exitCode);
            
        } finally {
            if (memoryMonitor != null) {
                memoryMonitor.stop();
            }
        }
    }
} 