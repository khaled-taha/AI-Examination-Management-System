package com.university.exam.examManagement.dtos.response;

import lombok.Data;
import java.util.UUID;

@Data
public class StudentCodingTestResultResponseDTO {
    private UUID id;
    private UUID studentAnswerCodeId;
    private UUID testCaseId;
    private Boolean passed;
    private Double markObtained;
    private Integer executionTimeMs;
    private Integer memoryUsedKb;
    private String feedback;
}