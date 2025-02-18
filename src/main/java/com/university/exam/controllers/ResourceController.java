package com.university.exam.controllers;

import com.university.exam.dtos.requestDTO.ResourceDirectoryRequestDTO;
import com.university.exam.dtos.responseDTO.DirectoryWithResourcesDTO;
import com.university.exam.dtos.responseDTO.ResourceDirectoryResponseDTO;
import com.university.exam.dtos.responseDTO.ResourceResponseDTO;
import com.university.exam.services.ResourceService;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.rmi.NoSuchObjectException;
import java.util.List;
import java.util.UUID;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/resources")
@Tag(name = "Resource Management", description = "APIs for managing resources and directories")
public class ResourceController {

    @Autowired
    private ResourceService resourceService;

    @PostMapping("/upload")
    @Operation(
            summary = "Upload a resource",
            description = "Uploads a resource file with the provided details.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Resource uploaded successfully",
                            content = @Content(schema = @Schema(implementation = ResourceResponseDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input"),
                    @ApiResponse(responseCode = "404", description = "Directory not found")
            }
    )
    public ResponseEntity<ResourceResponseDTO> uploadResource(
            @Parameter(description = "Resource file to upload", required = true)
            @RequestParam MultipartFile file,
            @Parameter(description = "ID of the resource directory", required = true)
            @RequestParam("resourceDirId") UUID resourceDirId) throws IOException {

        ResourceResponseDTO uploadedResource = resourceService.uploadResource(resourceDirId, file);
        return ResponseEntity.ok(uploadedResource);
    }

    @DeleteMapping("/{resourceId}")
    @Operation(
            summary = "Delete a resource",
            description = "Deletes the resource with the specified ID.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Resource deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "Resource not found")
            }
    )
    public ResponseEntity<Void> deleteResource(
            @Parameter(description = "ID of the resource to delete", required = true)
            @PathVariable UUID resourceId) throws NoSuchObjectException {
        resourceService.deleteResource(resourceId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/download/{resourceId}")
    @Operation(
            summary = "Download a resource",
            description = "Downloads the resource file with the specified ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Resource downloaded successfully",
                            content = @Content(schema = @Schema(type = "blob", format = "binary"))),
                    @ApiResponse(responseCode = "404", description = "Resource not found")
            }
    )
    public void downloadResource(
            @Parameter(description = "ID of the resource to download", required = true)
            @PathVariable UUID resourceId, HttpServletResponse response) throws IOException {
        ResourceService.FileDownloading fileDownloading = resourceService.downloadResource(resourceId);

        response.setContentType(fileDownloading.type());
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileDownloading.name() + "\"");
        response.getOutputStream().write(fileDownloading.data());
        response.getOutputStream().flush();
    }


    @GetMapping("/files/preview/{resourceId}")
    @Operation(
            summary = "Preview a resource In the browser",
            description = "Previews the resource file with the specified ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Resource downloaded successfully",
                            content = @Content(schema = @Schema(type = "blob", format = "binary"))),
                    @ApiResponse(responseCode = "404", description = "Resource not found")
            }
    )
    public ResponseEntity<byte[]> previewFile(
            @Parameter(description = "ID of the resource to download", required = true)
            @PathVariable UUID resourceId) throws IOException {
        ResourceService.FileDownloading fileDownloading = resourceService.downloadResource(resourceId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileDownloading.name() + "\"")
                .contentType(MediaType.parseMediaType(fileDownloading.type()))
                .body(fileDownloading.data());
    }

    @PostMapping("/directories")
    @Operation(
            summary = "Create a new directory",
            description = "Creates a new directory with the provided details.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Directory created successfully",
                            content = @Content(schema = @Schema(implementation = ResourceDirectoryResponseDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input")
            }
    )
    public ResponseEntity<ResourceDirectoryResponseDTO> createDirectory(@Valid @RequestBody ResourceDirectoryRequestDTO directoryDTO) {
        return ResponseEntity.ok(resourceService.createDirectory(directoryDTO));
    }

    @PutMapping("/directories/{directoryId}")
    @Operation(
            summary = "Update an existing directory",
            description = "Updates the directory with the specified ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Directory updated successfully",
                            content = @Content(schema = @Schema(implementation = ResourceDirectoryResponseDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input"),
                    @ApiResponse(responseCode = "404", description = "Directory not found")
            }
    )
    public ResponseEntity<ResourceDirectoryResponseDTO> updateDirectory(
            @Parameter(description = "ID of the directory to update", required = true)
            @PathVariable UUID directoryId,
            @Valid @RequestBody ResourceDirectoryRequestDTO directoryDTO) throws NoSuchObjectException {
        return ResponseEntity.ok(resourceService.updateDirectory(directoryId, directoryDTO));
    }

    @DeleteMapping("/directories/{directoryId}")
    @Operation(
            summary = "Delete a directory",
            description = "Deletes the directory with the specified ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Directory deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "Directory not found")
            }
    )
    public ResponseEntity<String> deleteDirectory(
            @Parameter(description = "ID of the directory to delete", required = true)
            @PathVariable UUID directoryId) throws NoSuchObjectException {
        resourceService.deleteDirectory(directoryId);
        return ResponseEntity.ok("Deleted Successfully!");
    }

    @GetMapping("/directories/{baseDirectoryId}")
    @Operation(
            summary = "Get sub-directories of a base dir",
            description = "Retrieves all sub-directories of a base dir. We can get all categorized resources of any course" +
                    " by baseDirectoryId in Course Response",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Sub Directories retrieved successfully",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = DirectoryWithResourcesDTO.class)))),
                    @ApiResponse(responseCode = "404", description = "Base Dir not found")
            }
    )
    public ResponseEntity<List<DirectoryWithResourcesDTO>> getSubDirectoriesByBaseId(
            @Parameter(description = "Id of the base Directory", required = true)
            @PathVariable UUID baseDirectoryId) throws NoSuchObjectException {
        List<DirectoryWithResourcesDTO> directoryWithResourcesDTOS = resourceService.getSubDirectoriesById(baseDirectoryId);
        return ResponseEntity.ok(directoryWithResourcesDTOS);
    }
}