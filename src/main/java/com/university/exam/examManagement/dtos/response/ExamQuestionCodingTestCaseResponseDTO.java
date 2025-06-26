package com.university.exam.examManagement.dtos.response;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
public class ExamQuestionCodingTestCaseResponseDTO {
    private UUID examQuestionId;
    private List<TestCase> testCases;

    @Data
    public static class TestCase {
        private UUID id;
        private String input;
        private String expectedOutput;
        private BigDecimal mark;
        private boolean sample;
    }
} 