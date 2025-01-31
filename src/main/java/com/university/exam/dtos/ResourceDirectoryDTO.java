package com.university.exam.dtos;

import com.university.exam.entities.ResourceDirectory;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.UUID;

@Data
public class ResourceDirectoryDTO {
    @NotBlank(message = "Directory name is required")
    @Size(max = 50, message = "Directory name cannot exceed 50 characters")
    private String name;

    @NotBlank(message = "Creator name is required")
    @Size(max = 50, message = "Creator name cannot exceed 50 characters")
    private String creator;

    private UUID baseDirId;

    public static ResourceDirectoryDTO fromEntity(ResourceDirectory directory) {
        ResourceDirectoryDTO dto = new ResourceDirectoryDTO();
        dto.setName(directory.getName());
        dto.setCreator(directory.getCreator());
        dto.setBaseDirId(directory.getBaseDirId());
        return dto;
    }
}
