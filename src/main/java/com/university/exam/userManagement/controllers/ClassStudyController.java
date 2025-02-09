package com.university.exam.userManagement.controllers;

import com.university.exam.userManagement.dtos.responseDTO.ClassStudyResponseDTO;
import com.university.exam.userManagement.repos.ClassStudyRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/class-studies")
public class ClassStudyController {
    @Autowired
    private ClassStudyRepository classStudyRepository;

    @GetMapping
    @Operation(
            summary = "Get all class studies",
            description = "Retrieves a list of all class studies.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Class studies retrieved successfully",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = ClassStudyResponseDTO.class))))
            }
    )
    public ResponseEntity<List<ClassStudyResponseDTO>> getAllClassStudies() {
        List<ClassStudyResponseDTO> dtoList = classStudyRepository.findAll()
                .stream()
                .map(ClassStudyResponseDTO::convertToClassStudyResponseDTO).toList();
        return ResponseEntity.ok(dtoList);
    }
}
