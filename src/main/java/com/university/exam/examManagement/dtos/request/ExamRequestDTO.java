package com.university.exam.examManagement.dtos.request;

import jakarta.persistence.Column;
import lombok.Data;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ExamRequestDTO {
    private UUID id;

    @NotBlank(message = "Exam title must not be blank")
    private String title;

    private String description;

    @NotNull(message = "Start date is required")
    @Future(message = "Start date must be in the future")
    private LocalDateTime startDate;

    @NotNull(message = "End date is required")
    @Future(message = "End date must be in the future")
    private LocalDateTime endDate;

    private String status;

    @NotNull(message = "Creator ID is required")
    private UUID creatorId;

    @NotNull(message = "Academic Year Course Id is required")
    private UUID academicYearCourseId;

    @NotNull(message = "Term ID is required")
    private UUID termId;

    @NotNull(message = "Academic year ID is required")
    private UUID academicYearId;

    @Min(value = 0, message = "Success percentage must be at least 0")
    @Max(value = 100, message = "Success percentage cannot exceed 100")
    private double successPercentage;

    @Min(value = 1, message = "Allowed attempt times must be at least 1")
    private int allowedAttemptTimes;

    private boolean showResult;
    private byte questionsPerPage;
}