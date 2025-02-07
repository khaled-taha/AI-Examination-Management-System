package com.university.exam.resourceManagement.dtos.responseDTO;

import com.university.exam.resourceManagement.entities.ResourceDirectory;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class DirectoryWithResourcesDTO {
    private UUID id;
    private String name;
    private String creator;
    private UUID baseDirId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<ResourceResponseDTO> resources;

    public static DirectoryWithResourcesDTO fromEntity(ResourceDirectory directory, List<ResourceResponseDTO> resources) {
        DirectoryWithResourcesDTO dto = new DirectoryWithResourcesDTO();
        dto.setId(directory.getId());
        dto.setName(directory.getName());
        dto.setCreator(directory.getCreator());
        dto.setBaseDirId(directory.getBaseDirId());
        dto.setCreatedAt(directory.getCreatedAt());
        dto.setUpdatedAt(directory.getUpdatedAt());
        dto.setResources(resources);
        return dto;
    }
}
