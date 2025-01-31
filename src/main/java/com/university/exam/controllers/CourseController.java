package com.university.exam.controllers;

import com.university.exam.dtos.CourseDTO;
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
    public ResponseEntity<CourseDTO> createCourse(@Valid @RequestBody CourseDTO courseDTO) throws NoSuchObjectException, ValidationException {
        return ResponseEntity.ok(courseService.createCourse(courseDTO));
    }

    @PutMapping("/{code}")
    public ResponseEntity<CourseDTO> updateCourse(@PathVariable String code, @Valid @RequestBody CourseDTO courseDTO) throws NoSuchObjectException, ValidationException {
        return ResponseEntity.ok(courseService.updateCourse(code, courseDTO));
    }

    @DeleteMapping("/{code}")
    public ResponseEntity<Void> deleteCourse(@PathVariable String code) throws NoSuchObjectException {
        courseService.deleteCourse(code);
        return ResponseEntity.noContent().build();
    }
}

