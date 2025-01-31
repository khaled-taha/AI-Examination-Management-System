package com.university.exam.controllers;

import com.university.exam.dtos.requestDTO.ResourceRequestDTO;
import com.university.exam.dtos.requestDTO.ResourceDirectoryRequestDTO;
import com.university.exam.dtos.responseDTO.ResourceDirectoryResponseDTO;
import com.university.exam.dtos.responseDTO.ResourceResponseDTO;
import com.university.exam.services.ResourceService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.rmi.NoSuchObjectException;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/resources")
public class ResourceController {
    @Autowired
    private ResourceService resourceService;

    @PostMapping("/upload")
    public ResponseEntity<ResourceResponseDTO> uploadResource(
            @RequestParam("file") MultipartFile file,
            @RequestParam("name") String name,
            @RequestParam("type") String type,
            @RequestParam("resourceDirId") UUID resourceDirId) throws IOException {
        ResourceRequestDTO resourceRequestDTO = new ResourceRequestDTO();
        resourceRequestDTO.setName(name);
        resourceRequestDTO.setType(type);
        resourceRequestDTO.setResourceDirId(resourceDirId);

        ResourceResponseDTO uploadedResource = resourceService.uploadResource(resourceRequestDTO, file.getBytes());
        return ResponseEntity.ok(uploadedResource);
    }

    @DeleteMapping("/{resourceId}")
    public ResponseEntity<Void> deleteResource(@PathVariable UUID resourceId) throws NoSuchObjectException {
        resourceService.deleteResource(resourceId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/download/{resourceId}")
    public ResponseEntity<byte[]> downloadResource(@PathVariable UUID resourceId) throws NoSuchObjectException {
        byte[] data = resourceService.downloadResource(resourceId);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"" + resourceId + "\"")
                .body(data);
    }

    @PostMapping("/directories")
    public ResponseEntity<ResourceDirectoryResponseDTO> createDirectory(@Valid @RequestBody ResourceDirectoryRequestDTO directoryDTO) {
        return ResponseEntity.ok(resourceService.createDirectory(directoryDTO));
    }

    @PutMapping("/directories/{directoryId}")
    public ResponseEntity<ResourceDirectoryResponseDTO> updateDirectory(
            @PathVariable UUID directoryId,
            @Valid @RequestBody ResourceDirectoryRequestDTO directoryDTO) throws NoSuchObjectException {
        return ResponseEntity.ok(resourceService.updateDirectory(directoryId, directoryDTO));
    }

    @DeleteMapping("/directories/{directoryId}")
    public ResponseEntity<Void> deleteDirectory(@PathVariable UUID directoryId) throws NoSuchObjectException {
        resourceService.deleteDirectory(directoryId);
        return ResponseEntity.noContent().build();
    }
}