package com.university.exam.userManagement.controllers;

import com.university.exam.userManagement.dtos.requestDTO.StudentRequestDTO;
import com.university.exam.userManagement.dtos.responseDTO.StudentResponseDTO;
import com.university.exam.userManagement.services.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/students")
public class StudentController {
    @Autowired
    private StudentService studentService;

    @GetMapping("/user/{userId}")
    @Operation(
            summary = "Get student by user ID",
            description = "Retrieves a student by the associated user ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Student retrieved successfully",
                            content = @Content(schema = @Schema(implementation = StudentResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Student not found")
            }
    )
    public ResponseEntity<StudentResponseDTO> getStudentByUserId(@PathVariable UUID userId) throws Exception {
        return ResponseEntity.ok(studentService.getStudentByUserId(userId));
    }

    @PostMapping
    @Operation(
            summary = "Create a new student",
            description = "Saves a new student with the provided details.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Student created successfully",
                            content = @Content(schema = @Schema(implementation = StudentResponseDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request payload")
            }
    )
    public ResponseEntity<StudentResponseDTO> saveStudent(@RequestBody StudentRequestDTO studentRequestDTO) throws Exception {
        return ResponseEntity.ok(studentService.saveStudent(studentRequestDTO));
    }
}