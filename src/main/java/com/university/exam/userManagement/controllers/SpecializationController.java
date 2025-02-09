package com.university.exam.userManagement.controllers;

import com.university.exam.userManagement.dtos.responseDTO.SpecializationResponseDTO;
import com.university.exam.userManagement.repos.SpecializationRepository;
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
@RequestMapping("/api/v1/specialization")
public class SpecializationController {
    @Autowired
    private SpecializationRepository specializationRepository;

    @GetMapping
    @Operation(
            summary = "Get all specializations",
            description = "Retrieves a list of all available specializations.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Specializations retrieved successfully",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = SpecializationResponseDTO.class))))
            }
    )
    public ResponseEntity<List<SpecializationResponseDTO>> getAllSpecializations() {
        List<SpecializationResponseDTO> dtoList = specializationRepository.findAll()
                .stream()
                .map(SpecializationResponseDTO::convertToSpecializationResponseDTO).toList();
        return ResponseEntity.ok(dtoList);
    }
}
