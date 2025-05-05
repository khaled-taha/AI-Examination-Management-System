package com.university.exam.resourceManagement.dtos.responseDTO;

import com.university.exam.resourceManagement.entities.ResourceDirectory;
import com.university.exam.resourceManagement.entities.Resource;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class BaseDirResponseDTO {
    private UUID baseId; // The Id of the base (root) dir
    private String name;
    private String creator;
    private UUID baseDirId; // Parent Dir ID. (It will be null if the current dir is the root (base dir))
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<ResourceResponseDTO> baseResources; // the resources of the root such as the course avatar
    private List<DirectoryWithResourcesDTO> subDirectory; // all dirs of this root such as assignments.


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