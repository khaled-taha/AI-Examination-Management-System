package com.university.exam.security.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/test")
@Tag(name = "Test", description = "Test endpoints for security demonstration")
public class TestController {

    @GetMapping("/superadmin")
    @PreAuthorize("hasRole('SUPERADMIN')")
    @Operation(summary = "SuperAdmin only endpoint")
    public ResponseEntity<Map<String, String>> superAdminOnly() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "SuperAdmin access granted");
        response.put("role", "SUPERADMIN");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Admin and SuperAdmin endpoint")
    public ResponseEntity<Map<String, String>> adminOnly() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Admin access granted");
        response.put("role", "ADMIN");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/viewer")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'VIEWER')")
    @Operation(summary = "Viewer, Admin and SuperAdmin endpoint")
    public ResponseEntity<Map<String, String>> viewerOnly() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Viewer access granted");
        response.put("role", "VIEWER");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/student")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'VIEWER', 'STUDENT')")
    @Operation(summary = "Student, Viewer, Admin and SuperAdmin endpoint")
    public ResponseEntity<Map<String, String>> studentOnly() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Student access granted");
        response.put("role", "STUDENT");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/public")
    @Operation(summary = "Public endpoint - no authentication required")
    public ResponseEntity<Map<String, String>> publicEndpoint() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Public access granted");
        response.put("role", "PUBLIC");
        return ResponseEntity.ok(response);
    }
} 