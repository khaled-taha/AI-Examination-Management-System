package com.university.exam.examManagement.dtos.response;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ExamResponseDTO {
    private UUID id;
    private String title;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String status;
    private UUID creatorId;
    private String courseCode;
    private UUID termId;
    private UUID academicYearGroupId;
    private double successPercentage;
    private int allowedAttemptTimes;
    private boolean showResult;
    private byte questionsPerPage;
    private LocalDateTime creationTime;
} 