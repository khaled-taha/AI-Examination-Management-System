package com.university.exam.dtos.requestDTO;

import lombok.Data;

import jakarta.validation.constraints.*;
import java.util.UUID;

@Data
public class CourseRequestDTO {
    @NotBlank(message = "Course code is required")
    @Size(min = 5, max = 10, message = "Course code must be between 5 and 10 characters")
    private String code;

    @NotBlank(message = "Course name is required")
    @Size(max = 100, message = "Course name cannot exceed 100 characters")
    private String name;

    @NotNull(message = "Group ID is required")
    private UUID groupId;

    private byte[] avatar;
    private String avatarType;

    private boolean active = true;
}