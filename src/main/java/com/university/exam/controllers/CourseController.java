package com.university.exam.controllers;

import com.university.exam.dtos.requestDTO.CourseRequestDTO;
import com.university.exam.dtos.responseDTO.CourseResponseDTO;
import com.university.exam.dtos.responseDTO.DirectoryWithResourcesDTO;
import com.university.exam.exceptions.ValidationException;
import com.university.exam.services.CourseService;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Validated
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/courses")
@Tag(name = "Course Management", description = "APIs for managing courses")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Create a new course",
            description = "Creates a new course with the provided details.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Course created successfully",
                            content = @Content(schema = @Schema(implementation = CourseResponseDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input"),
                    @ApiResponse(responseCode = "404", description = "Related Group not found")
            }
    )
    public ResponseEntity<CourseResponseDTO> createCourse(
            @RequestParam @NotBlank(message = "Course code is required")
                          @Size(min = 5, max = 10, message = "Course code must be between 5 and 10 characters") String code,
            @RequestParam @NotBlank(message = "Course name is required")
                          @Size(min = 5, max = 100, message = "Course name cannot exceed 100 characters") String name,
            @RequestParam @NotNull(message = "Group ID is required") UUID groupId,
            @RequestParam(required = false) MultipartFile avatar) throws IOException, ValidationException {

        CourseRequestDTO courseRequestDTO = new CourseRequestDTO();
        courseRequestDTO.setCode(code);
        courseRequestDTO.setGroupId(groupId);
        courseRequestDTO.setName(name);

        return ResponseEntity.ok(courseService.createCourse(courseRequestDTO, avatar));
    }


    @PutMapping(path = "/{courseCode}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Update an existing course",
            description = "Updates the course with the specified code.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Course updated successfully",
                            content = @Content(schema = @Schema(implementation = CourseResponseDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input"),
                    @ApiResponse(responseCode = "404", description = "Course not found")
            }
    )
    public ResponseEntity<CourseResponseDTO> updateCourse(
            @PathVariable @NotBlank(message = "Course code is required") String courseCode,

            @RequestParam @NotBlank(message = "Course name is required")
            @Size(min = 5, max = 100, message = "Course name cannot exceed 100 characters") String name,

            @RequestParam @NotNull(message = "Group ID is required") UUID groupId,
            @RequestParam(required = false) MultipartFile avatar) throws IOException, ValidationException {

        CourseRequestDTO courseRequestDTO = new CourseRequestDTO();
        courseRequestDTO.setGroupId(groupId);
        courseRequestDTO.setName(name);

        return ResponseEntity.ok(courseService.updateCourse(courseCode, courseRequestDTO, avatar));
    }

    @DeleteMapping("/{code}")
    @Operation(
            summary = "Delete a course",
            description = "Deletes the course with the specified code.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Course deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "Course not found")
            }
    )
    public ResponseEntity<String> deleteCourse(
            @Parameter(description = "Code of the course to delete", required = true)
            @PathVariable String code) throws NoSuchObjectException {
        courseService.deleteCourse(code);
        return ResponseEntity.ok("Deleted Successfully!");
    }

    @GetMapping("/{code}/directories")
    @Deprecated
    @Operation(
            summary = "Get directories for a course",
            description = "Retrieves all directories associated with the specified course code.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Directories retrieved successfully",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = DirectoryWithResourcesDTO.class)))),

                    @ApiResponse(responseCode = "404", description = "Course not found")
            }
    )
    public ResponseEntity<List<DirectoryWithResourcesDTO>> getDirectoriesByCourseCode(
            @Parameter(description = "Code of the course", required = true)
            @PathVariable String code) throws NoSuchObjectException {
        List<DirectoryWithResourcesDTO> directoryWithResourcesDTOS = courseService.getDirectoriesByCourseCode(code);
        return ResponseEntity.ok(directoryWithResourcesDTOS);
    }

    @GetMapping("/group/{groupId}")
    @Operation(
            summary = "Get courses by group ID",
            description = "Retrieves all courses associated with the specified group ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Courses retrieved successfully",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = CourseResponseDTO.class)))),
                    @ApiResponse(responseCode = "404", description = "Group not found")
            }
    )
    public ResponseEntity<List<CourseResponseDTO>> getCoursesByGroupId(
            @Parameter(description = "ID of the group", required = true)
            @PathVariable UUID groupId) throws NoSuchObjectException {
        return ResponseEntity.ok(courseService.getCoursesByGroupId(groupId));
    }

    @GetMapping("/{code}")
    @Operation(
            summary = "Retrieve a course",
            description = "Retrieve the course by the specified code.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Course retrieved successfully"),
                    @ApiResponse(responseCode = "404", description = "Course not found")
            }
    )
    public ResponseEntity<CourseResponseDTO> getCourse(
            @Parameter(description = "Code of the course to retrieve", required = true)
            @PathVariable String code) throws NoSuchObjectException {
        return ResponseEntity.ok(courseService.getCourseByCode(code));
    }
}
