package com.university.exam.controllers;

import com.university.exam.dtos.ResourceDTO;
import com.university.exam.dtos.ResourceDirectoryDTO;
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
    public ResponseEntity<ResourceDTO> uploadResource(
            @RequestParam("file") MultipartFile file,
            @RequestParam("name") String name,
            @RequestParam("type") String type,
            @RequestParam("resourceDirId") UUID resourceDirId) throws IOException {
        ResourceDTO resourceDTO = new ResourceDTO();
        resourceDTO.setName(name);
        resourceDTO.setType(type);
        resourceDTO.setResourceDirId(resourceDirId);

        ResourceDTO uploadedResource = resourceService.uploadResource(resourceDTO, file.getBytes());
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
    public ResponseEntity<ResourceDirectoryDTO> createDirectory(@Valid @RequestBody ResourceDirectoryDTO directoryDTO) {
        return ResponseEntity.ok(resourceService.createDirectory(directoryDTO));
    }

    @PutMapping("/directories/{directoryId}")
    public ResponseEntity<ResourceDirectoryDTO> updateDirectory(
            @PathVariable UUID directoryId,
            @Valid @RequestBody ResourceDirectoryDTO directoryDTO) throws NoSuchObjectException {
        return ResponseEntity.ok(resourceService.updateDirectory(directoryId, directoryDTO));
    }

    @DeleteMapping("/directories/{directoryId}")
    public ResponseEntity<Void> deleteDirectory(@PathVariable UUID directoryId) throws NoSuchObjectException {
        resourceService.deleteDirectory(directoryId);
        return ResponseEntity.noContent().build();
    }
}