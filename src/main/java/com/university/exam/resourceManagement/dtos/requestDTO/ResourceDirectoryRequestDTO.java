package com.university.exam.resourceManagement.dtos.requestDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.UUID;

@Data
public class ResourceDirectoryRequestDTO {
    @NotBlank(message = "Directory name is required")
    @Size(max = 50, message = "Directory name cannot exceed 50 characters")
    private String name;

    @NotBlank(message = "Creator name is required")
    @Size(max = 50, message = "Creator name cannot exceed 50 characters")
    private String creator;

    private UUID baseDirId;
}
