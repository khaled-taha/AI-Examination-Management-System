package com.university.exam.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import java.time.Duration;

@Configuration
public class CodeEvaluationConfig {

    @Value("${code.evaluation.service.url:http://localhost:8084}")
    private String codeEvaluationServiceUrl;

    @Value("${code.evaluation.service.timeout:30000}")
    private int timeoutMs;

    @Bean
    public RestTemplate codeEvaluationRestTemplate(RestTemplateBuilder builder) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(timeoutMs);
        factory.setReadTimeout(timeoutMs);

        return builder
                .requestFactory(() -> factory)
                .setConnectTimeout(Duration.ofMillis(timeoutMs))
                .setReadTimeout(Duration.ofMillis(timeoutMs))
                .build();
    }

    @Bean
    public String codeEvaluationServiceUrl() {
        return codeEvaluationServiceUrl;
    }
} 