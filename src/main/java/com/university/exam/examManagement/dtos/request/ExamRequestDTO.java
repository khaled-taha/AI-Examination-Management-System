package com.university.exam.examManagement.dtos.request;

import lombok.Data;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ExamRequestDTO {
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

    @NotBlank(message = "Course code is required")
    private String courseCode;

    @NotNull(message = "Term ID is required")
    private UUID termId;

    @NotNull(message = "Academic year group ID is required")
    private UUID academicYearGroupId;

    @Min(value = 0, message = "Success percentage must be at least 0")
    @Max(value = 100, message = "Success percentage cannot exceed 100")
    private double successPercentage;

    @Min(value = 1, message = "Allowed attempt times must be at least 1")
    private int allowedAttemptTimes;
}