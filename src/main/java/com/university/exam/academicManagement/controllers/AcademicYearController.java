package com.university.exam.academicManagement.controllers;

import com.university.exam.academicManagement.dtos.requestDTO.AcademicYearCourseRequestDTO;
import com.university.exam.academicManagement.dtos.requestDTO.AcademicYearRequestDTO;
import com.university.exam.academicManagement.dtos.responseDTO.AcademicYearCourseResponseDTO;
import com.university.exam.academicManagement.dtos.responseDTO.AcademicYearResponseDTO;
import com.university.exam.academicManagement.entities.AcademicYearGroup;
import com.university.exam.academicManagement.services.AcademicYearService;
import com.university.exam.userManagement.dtos.responseDTO.StudentResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/academic-years")
@RequiredArgsConstructor
@Tag(name = "Academic Year Management", description = "APIs for managing academic years")
public class AcademicYearController {

    private final AcademicYearService academicYearService;

    @PostMapping
    @Operation(
            summary = "save an academic year for a group",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Academic year group saved successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid input")
            }
    )
    public ResponseEntity<AcademicYearResponseDTO.SaveYearResponse> saveAcademicYearGroup(
            @RequestBody AcademicYearRequestDTO.SaveYearRequest request) {
        return ResponseEntity.ok(academicYearService.saveAcademicYearGroup(request));
    }

    @PostMapping("/academicYear/terms")
    @Operation(
            summary = "Save terms for an academic year",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Terms saved successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid input"),
                    @ApiResponse(responseCode = "404", description = "Academic year not found")
            }
    )
    public ResponseEntity<AcademicYearResponseDTO.SaveAcademicYearTermsResponseDTO> saveAcademicYearTerms(
            @RequestBody AcademicYearRequestDTO.SaveTermsRequest request) {

        request.setAcademicYearId(request.getAcademicYearId());
        return ResponseEntity.ok(academicYearService.saveAcademicYearTerms(request));
    }

    @PostMapping("/academic-year-courses")
    @Operation(
            summary = "Assign courses to academic term",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Courses assigned successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid input"),
                    @ApiResponse(responseCode = "404", description = "Academic year or course not found")
            }
    )
    public ResponseEntity<List<AcademicYearCourseResponseDTO>> assignCoursesToTerm(@RequestBody AcademicYearCourseRequestDTO request) {
        return ResponseEntity.ok(academicYearService.assignCoursesToTerm(request));
    }

    @GetMapping("/{academicYearId}")
    @Operation(summary = "Get academic year details with terms and courses")
    public ResponseEntity<AcademicYearResponseDTO> getAcademicYear(@PathVariable UUID academicYearId) {
        return ResponseEntity.ok(academicYearService.getAcademicYear(academicYearId));
    }

    @GetMapping("/{academicYearId}/terms")
    @Operation(summary = "Get all terms of an academic year")
    public ResponseEntity<List<AcademicYearResponseDTO.TermResponse>> getAcademicYearTerms(
            @PathVariable UUID academicYearId) {
        return ResponseEntity.ok(academicYearService.getAcademicYearTerms(academicYearId));
    }

    @GetMapping("/group/{groupId}")
    @Operation(summary = "Get all academic years of a group")
    public ResponseEntity<List<AcademicYearResponseDTO>> getAcademicYearsOfGroup(
            @PathVariable UUID groupId) {
        return ResponseEntity.ok(academicYearService.getAcademicYearsOfGroup(groupId));
    }

    @GetMapping("/{academicYearId}/courses")
    public ResponseEntity<List<AcademicYearCourseResponseDTO>> getAcademicYearCourses(
            @PathVariable UUID academicYearId) {
        return ResponseEntity.ok(academicYearService.getAcademicYearCourses(academicYearId));
    }

    @GetMapping("/academic-years/{academicYearId}/students")
    public ResponseEntity<List<StudentResponseDTO>> getStudentsByAcademicYear(@PathVariable UUID academicYearId) {
        return ResponseEntity.ok(academicYearService.getStudentsByAcademicYear(academicYearId));
    }

    @GetMapping("/academic-years/{academicYearId}/academicYearGroupId")
    public ResponseEntity<UUID> getAcademicYearGroup(@PathVariable UUID academicYearId) {
        return ResponseEntity.ok(academicYearService.getAcademicYearGroup(academicYearId));
    }
}