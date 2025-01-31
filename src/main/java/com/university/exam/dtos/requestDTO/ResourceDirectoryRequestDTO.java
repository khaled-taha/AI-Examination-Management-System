package com.university.exam.dtos.requestDTO;

import com.university.exam.entities.ResourceDirectory;
import jakarta.validation.constraints.*;
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
