package com.university.exam.academicManagement.controllers;

import com.university.exam.academicManagement.dtos.requestDTO.AcademicYearCourseAdminRequestDTO;
import com.university.exam.academicManagement.dtos.responseDTO.AcademicYearCourseAdminResponseDTO;
import com.university.exam.academicManagement.services.AcademicYearCourseAdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/academic-year-course-admins")
@RequiredArgsConstructor
@Tag(name = "Academic Year Course Admin Management", 
     description = "APIs for managing course administrators for academic year courses")
public class AcademicYearCourseAdminController {

    private final AcademicYearCourseAdminService academicYearCourseAdminService;

    @PostMapping
    @Operation(
        summary = "Assign an admin to a course",
        description = "Assigns a specific admin to manage a course for a given academic year. " +
                     "The admin will be responsible for managing the course content, exams, and student progress.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Admin assigned successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input or admin already assigned"),
            @ApiResponse(responseCode = "404", description = "Course or admin not found")
        }
    )
    public ResponseEntity<AcademicYearCourseAdminResponseDTO.CourseAdminResponse> assignAdminToCourse(
            @RequestBody AcademicYearCourseAdminRequestDTO.AssignAdminRequest request) {
        return ResponseEntity.ok(academicYearCourseAdminService.assignAdminToCourse(request));
    }

    @GetMapping("/course/{courseId}")
    @Operation(
        summary = "Get all admins for a course",
        description = "Retrieves a list of all administrators assigned to a specific course. " +
                     "This includes their basic information and assignment details.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved course admins"),
            @ApiResponse(responseCode = "404", description = "Course not found")
        }
    )
    public ResponseEntity<List<AcademicYearCourseAdminResponseDTO.CourseAdminResponse>> getAdminsByCourse(
            @PathVariable UUID courseId) {
        return ResponseEntity.ok(academicYearCourseAdminService.getCourseAdmins(courseId));
    }

    @GetMapping("/admin/{adminId}")
    @Operation(
        summary = "Get all courses for an admin",
        description = "Retrieves a list of all courses that a specific admin is assigned to manage. " +
                     "This includes course details and assignment information.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved admin's courses"),
            @ApiResponse(responseCode = "404", description = "Admin not found")
        }
    )
    public ResponseEntity<List<AcademicYearCourseAdminResponseDTO.CourseAdminResponse>> getCoursesByAdmin(
            @PathVariable UUID adminId) {
        return ResponseEntity.ok(academicYearCourseAdminService.getAdminCourses(adminId));
    }

    @DeleteMapping("/remove")
    @Operation(
        summary = "Remove admin from course",
        description = "Removes an admin's assignment from a specific course. " +
                     "This will revoke their administrative privileges for that course.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Admin removed successfully"),
            @ApiResponse(responseCode = "404", description = "Assignment not found")
        }
    )
    public ResponseEntity<Void> removeAdminFromCourse(@RequestBody AcademicYearCourseAdminRequestDTO.RemoveAdminRequest request) {
        academicYearCourseAdminService.removeAdminFromCourse(request);
        return ResponseEntity.ok().build();
    }
}