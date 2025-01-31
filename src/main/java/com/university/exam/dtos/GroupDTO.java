package com.university.exam.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.UUID;

@Data
public class GroupDTO {
    @NotBlank(message = "Group name is required")
    @Size(max = 50, message = "Group name cannot exceed 50 characters")
    private String name;
    private String description;
}
