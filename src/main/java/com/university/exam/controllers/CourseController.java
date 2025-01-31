package com.university.exam.controllers;

import com.university.exam.dtos.requestDTO.CourseRequestDTO;
import com.university.exam.dtos.responseDTO.CourseResponseDTO;
import com.university.exam.exceptions.ValidationException;
import com.university.exam.services.CourseService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.rmi.NoSuchObjectException;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/courses")
public class CourseController {
    @Autowired
    private CourseService courseService;

    @PostMapping
    public ResponseEntity<CourseResponseDTO> createCourse(@Valid @RequestBody CourseRequestDTO courseRequestDTO) throws NoSuchObjectException, ValidationException {
        return ResponseEntity.ok(courseService.createCourse(courseRequestDTO));
    }

    @PutMapping("/{code}")
    public ResponseEntity<CourseResponseDTO> updateCourse(@PathVariable String code, @Valid @RequestBody CourseRequestDTO courseRequestDTO) throws NoSuchObjectException, ValidationException {
        return ResponseEntity.ok(courseService.updateCourse(code, courseRequestDTO));
    }

    @DeleteMapping("/{code}")
    public ResponseEntity<Void> deleteCourse(@PathVariable String code) throws NoSuchObjectException {
        courseService.deleteCourse(code);
        return ResponseEntity.noContent().build();
    }
}

