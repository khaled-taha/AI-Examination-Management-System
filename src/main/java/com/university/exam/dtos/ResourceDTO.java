package com.university.exam.dtos;

import com.university.exam.entities.Resource;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.UUID;

@Data
public class ResourceDTO {
    @NotBlank(message = "Resource name is required")
    @Size(max = 100, message = "Resource name cannot exceed 100 characters")
    private String name;

    @NotBlank(message = "Resource type is required")
    @Size(max = 50, message = "Resource type cannot exceed 50 characters")
    private String type;

    @NotNull(message = "Resource directory ID is required")
    private UUID resourceDirId;

    public static ResourceDTO fromEntity(Resource resource) {
        ResourceDTO dto = new ResourceDTO();
        dto.setName(resource.getName());
        dto.setType(resource.getType());
        dto.setResourceDirId(resource.getResourceDirectory().getId());
        return dto;
    }
}
