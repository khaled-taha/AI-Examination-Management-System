package com.university.exam.userManagement.controllers;

import com.university.exam.userManagement.dtos.responseDTO.SpecializationResponseDTO;
import com.university.exam.userManagement.services.SpecializationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/specialization")
public class SpecializationController {
    @Autowired
    private SpecializationService specializationService;

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'VIEWER')")
    @Operation(
            summary = "Get all specializations",
            description = "Retrieves a list of all available specializations.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Specializations retrieved successfully",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = SpecializationResponseDTO.class))))
            }
    )
    public ResponseEntity<List<SpecializationResponseDTO>> getAllSpecializations() {
        return ResponseEntity.ok(specializationService.getAllSpecializations());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    public ResponseEntity<SpecializationResponseDTO> createSpecialization(@RequestParam String specializationName) {
        SpecializationResponseDTO createdSpecialization = specializationService.createSpecialization(specializationName);
        return new ResponseEntity<>(createdSpecialization, HttpStatus.CREATED);
    }

    @DeleteMapping("/{specializationId}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    public ResponseEntity<Void> deleteSpecialization(@PathVariable UUID specializationId) {
        specializationService.deleteSpecialization(specializationId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
