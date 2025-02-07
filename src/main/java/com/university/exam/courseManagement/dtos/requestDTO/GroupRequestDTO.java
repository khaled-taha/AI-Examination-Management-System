package com.university.exam.courseManagement.dtos.requestDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class GroupRequestDTO {
    @NotBlank(message = "Group name is required")
    @Size(max = 50, message = "Group name cannot exceed 50 characters")
    private String name;
    private String description;
}
