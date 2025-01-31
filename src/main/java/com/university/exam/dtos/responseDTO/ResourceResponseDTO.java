package com.university.exam.dtos.responseDTO;

import com.university.exam.entities.Resource;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ResourceResponseDTO {
    private UUID id;
    private String name;
    private String type;
    private UUID resourceDirId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ResourceResponseDTO fromEntity(Resource resource) {
        ResourceResponseDTO dto = new ResourceResponseDTO();
        dto.setId(resource.getId());
        dto.setName(resource.getName());
        dto.setType(resource.getType());
        dto.setResourceDirId(resource.getResourceDirectory().getId());
        dto.setCreatedAt(resource.getCreatedAt());
        dto.setUpdatedAt(resource.getUpdatedAt());
        return dto;
    }
}