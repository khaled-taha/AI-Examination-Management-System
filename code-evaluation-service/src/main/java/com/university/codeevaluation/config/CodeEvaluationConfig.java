package com.university.codeevaluation.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "code-evaluation")
public class CodeEvaluationConfig {
    
    private Timeout timeout = new Timeout();
    private Memory maxMemory = new Memory();
    private String tempDir = System.getProperty("java.io.tmpdir") + "/code-evaluation";
    
    @Data
    public static class Timeout {
        private int seconds = 10;
    }
    
    @Data
    public static class Memory {
        private int mb = 512;
    }
} 