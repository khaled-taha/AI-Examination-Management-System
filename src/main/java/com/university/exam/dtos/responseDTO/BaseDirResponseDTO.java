package com.university.exam.dtos.responseDTO;

import com.university.exam.entities.Resource;
import com.university.exam.entities.ResourceDirectory;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class BaseDirResponseDTO {
    private UUID baseId;
    private String name;
    private String creator;
    private UUID baseDirId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<ResourceResponseDTO> baseResources;
    private List<DirectoryWithResourcesDTO> subDirectory;


    public static BaseDirResponseDTO fromEntity(ResourceDirectory baseDir, List<Resource> resources, List<DirectoryWithResourcesDTO> directoryWithResourcesDTOS) {
        BaseDirResponseDTO responseDTO = new BaseDirResponseDTO();
        responseDTO.setBaseId(baseDir.getId());
        responseDTO.setName(baseDir.getName());
        responseDTO.setCreator(baseDir.getCreator());
        responseDTO.setBaseDirId(baseDir.getBaseDirId());
        responseDTO.setCreatedAt(baseDir.getCreatedAt());
        responseDTO.setUpdatedAt(baseDir.getUpdatedAt());

        List<ResourceResponseDTO> baseResourceResponse = resources.stream()
                .map(ResourceResponseDTO::fromEntity)
                .toList();

        responseDTO.setBaseResources(baseResourceResponse);
        responseDTO.setSubDirectory(directoryWithResourcesDTOS);
        return responseDTO;
    }
}
