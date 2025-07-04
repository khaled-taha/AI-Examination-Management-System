package com.university.exam.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

@Configuration
public class CodeEvaluationConfig {

    @Value("${code.evaluation.service.url:http://localhost:8080}")
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
                .build();
    }

    @Bean
    public String codeEvaluationServiceUrl() {
        return codeEvaluationServiceUrl;
    }
}