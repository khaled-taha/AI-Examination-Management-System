package com.university.codeevaluation.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Data
@Configuration
@ConfigurationProperties(prefix = "code-evaluation")
public class CodeEvaluationConfig {
    
    private Timeout timeout = new Timeout();
    private Memory maxMemory = new Memory();
    private String tempDir = System.getProperty("java.io.tmpdir") + "/code-evaluation";
    private Map<String, LanguageConfig> languages = new HashMap<>();
    
    public CodeEvaluationConfig() {
        // Initialize default language configurations
        languages.put("java", new LanguageConfig(10, 512, true));
        languages.put("python", new LanguageConfig(10, 256, false));
        languages.put("c++", new LanguageConfig(10, 512, true));
        languages.put("c", new LanguageConfig(10, 512, true));
        languages.put("sql", new LanguageConfig(30, 1024, false));
    }
    
    @Data
    public static class Timeout {
        private int seconds = 10;
    }
    
    @Data
    public static class Memory {
        private int mb = 512;
    }
    
    @Data
    public static class LanguageConfig {
        private int defaultTimeLimitSeconds;
        private int defaultMemoryLimitMb;
        private boolean requiresCompilation;
        
        public LanguageConfig() {}
        
        public LanguageConfig(int defaultTimeLimitSeconds, int defaultMemoryLimitMb, boolean requiresCompilation) {
            this.defaultTimeLimitSeconds = defaultTimeLimitSeconds;
            this.defaultMemoryLimitMb = defaultMemoryLimitMb;
            this.requiresCompilation = requiresCompilation;
        }
    }
    
    public LanguageConfig getLanguageConfig(String language) {
        return languages.getOrDefault(language.toLowerCase(), 
            new LanguageConfig(timeout.getSeconds(), maxMemory.getMb(), false));
    }
    
    public int getTimeLimitForLanguage(String language) {
        LanguageConfig config = getLanguageConfig(language);
        return config.getDefaultTimeLimitSeconds();
    }
    
    public int getMemoryLimitForLanguage(String language) {
        LanguageConfig config = getLanguageConfig(language);
        return config.getDefaultMemoryLimitMb();
    }
} 