package com.university.codeevaluation.utils;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;

@Slf4j
@Data
public class ProcessMemoryMonitor {
    private final Process process;
    private final int memoryLimitMb;
    private volatile boolean running = false;
    private volatile boolean memoryLimitExceeded = false;
    private volatile long maxMemoryUsed = 0;
    private Thread monitorThread;

    public ProcessMemoryMonitor(Process process, int memoryLimitMb) {
        this.process = process;
        this.memoryLimitMb = memoryLimitMb;
    }

    public void start() {
        if (running) return;

        running = true;
        monitorThread = new Thread(() -> {
            try {
                while (running && process.isAlive()) {
                    long memoryUsedKb = getProcessMemoryUsage();
                    maxMemoryUsed = Math.max(maxMemoryUsed, memoryUsedKb);

                    // Convert memory limit from MB to KB for comparison
                    long memoryLimitKb = memoryLimitMb * 1024;

                    log.debug("Memory check - Used: {} KB, Limit: {} KB, Max so far: {} KB",
                            memoryUsedKb, memoryLimitKb, maxMemoryUsed);

                    // Sanity check - if memory usage seems unreasonably high, skip memory monitoring
                    if (memoryUsedKb > 1000000) { // More than 1GB
                        log.warn("Suspiciously high memory usage reported: {} KB, skipping memory limit check", memoryUsedKb);
                        break;
                    }

                    if (memoryUsedKb > memoryLimitKb) {
                        log.warn("Memory limit exceeded! Used: {} KB, Limit: {} KB", memoryUsedKb, memoryLimitKb);
                        memoryLimitExceeded = true;
                        process.destroyForcibly();
                        break;
                    }

                    Thread.sleep(100); // Check every 100ms
                }
            } catch (Exception e) {
                log.warn("Error monitoring process memory: {}", e.getMessage());
            }
        });
        monitorThread.setDaemon(true);
        monitorThread.start();
    }

    public void stop() {
        running = false;
        if (monitorThread != null) {
            monitorThread.interrupt();
        }
    }

    private long getProcessMemoryUsage() {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                return getWindowsProcessMemory(process.pid());
            } else {
                return getUnixProcessMemory(process.pid());
            }
        } catch (Exception e) {
            log.warn("Could not get process memory usage: {}", e.getMessage());
            return 0;
        }
    }

    private long getWindowsProcessMemory(long pid) throws Exception {
        // Try wmic first as it's more reliable
        try {
            return getWindowsProcessMemoryWmic(pid);
        } catch (Exception e) {
            log.debug("WMIC failed, falling back to tasklist: {}", e.getMessage());
            return getWindowsProcessMemoryTasklist(pid);
        }
    }

    private long getWindowsProcessMemoryWmic(long pid) throws Exception {
        ProcessBuilder pb = new ProcessBuilder("wmic", "process", "where", "ProcessId=" + pid, "get", "WorkingSetSize", "/format:value");
        Process p = pb.start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("WorkingSetSize=")) {
                    String memoryStr = line.substring("WorkingSetSize=".length()).trim();
                    if (!memoryStr.isEmpty()) {
                        long memoryBytes = Long.parseLong(memoryStr);
                        long memoryKb = memoryBytes / 1024;
                        log.debug("Process {} memory usage (WMIC): {} KB", pid, memoryKb);
                        return memoryKb;
                    }
                }
            }
        }
        throw new Exception("Could not get memory usage via WMIC");
    }

    private long getWindowsProcessMemoryTasklist(long pid) throws Exception {
        ProcessBuilder pb = new ProcessBuilder("tasklist", "/FI", "PID eq " + pid, "/FO", "CSV");
        Process p = pb.start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                log.debug("tasklist output line: {}", line);
                if (line.contains("," + pid + ",")) {
                    // Parse memory usage from tasklist output
                    // Format: "Image Name","PID","Session Name","Session#","Mem Usage"
                    String[] parts = line.split(",");
                    log.debug("Parsed parts: {}", java.util.Arrays.toString(parts));
                    if (parts.length >= 5) {
                        // Remove quotes and extract just the number
                        String memoryStr = parts[4].replaceAll("\"", "").replaceAll("[^0-9]", "");
                        log.debug("Extracted memory string: '{}'", memoryStr);
                        if (!memoryStr.isEmpty()) {
                            long memoryKb = Long.parseLong(memoryStr);
                            log.debug("Process {} memory usage (tasklist): {} KB", pid, memoryKb);

                            // Sanity check - if memory usage seems too high, log a warning
                            if (memoryKb > 1000000) { // More than 1GB
                                log.warn("Suspiciously high memory usage reported: {} KB for process {}", memoryKb, pid);
                            }

                            return memoryKb;
                        }
                    }
                }
            }
        }
        log.warn("Could not find memory usage for process {}", pid);
        return 0;
    }

    private long getUnixProcessMemory(long pid) throws Exception {
        ProcessBuilder pb = new ProcessBuilder("ps", "-p", String.valueOf(pid), "-o", "rss=");
        Process p = pb.start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
            String line = reader.readLine();
            log.debug("ps output for PID {}: '{}'", pid, line);
            if (line != null && !line.trim().isEmpty()) {
                long memoryKb = Long.parseLong(line.trim());
                log.debug("Process {} memory usage: {} KB", pid, memoryKb);
                return memoryKb;
            }
        }
        log.warn("Could not get memory usage for process {}", pid);
        return 0;
    }
}
