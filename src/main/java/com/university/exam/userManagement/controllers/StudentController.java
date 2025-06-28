package com.university.exam.userManagement.controllers;

import com.university.exam.userManagement.dtos.requestDTO.StudentRequestDTO;
import com.university.exam.userManagement.dtos.responseDTO.StudentResponseDTO;
import com.university.exam.userManagement.services.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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


    @GetMapping("/{email}")
    @Operation(
            summary = "Get student by email",
            description = "Retrieves a student by the associated email.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Student retrieved successfully",
                            content = @Content(schema = @Schema(implementation = StudentResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Student not found")
            }
    )
    public ResponseEntity<StudentResponseDTO> getStudentByUserEmail(@PathVariable String email) throws Exception {
        return ResponseEntity.ok(studentService.getStudentByEmail(email));
    }

    @GetMapping
    public ResponseEntity<List<StudentResponseDTO>> listStudents()  {
        return ResponseEntity.ok(studentService.getAllStudents());
    }


    @PostMapping
    @Operation(
            summary = "Save a student",
            description = "Save a student with the provided details.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Student created successfully",
                            content = @Content(schema = @Schema(implementation = StudentResponseDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request payload")
            }
    )
    public ResponseEntity<StudentResponseDTO> saveStudent(@Valid @RequestBody StudentRequestDTO studentRequestDTO) throws Exception {
        return ResponseEntity.ok(studentService.saveStudent(studentRequestDTO));
    }
}