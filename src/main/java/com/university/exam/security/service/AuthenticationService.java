package com.university.exam.security.service;

import com.university.exam.exceptions.ValidationException;
import com.university.exam.security.dto.AuthenticationRequest;
import com.university.exam.security.dto.AuthenticationResponse;
import com.university.exam.security.dto.AuthenticationResponseBuilder;
import com.university.exam.security.model.CustomUserDetails;
import com.university.exam.userManagement.dtos.responseDTO.AdminResponseDTO;
import com.university.exam.userManagement.dtos.responseDTO.StudentResponseDTO;
import com.university.exam.userManagement.repos.UserRepository;
import com.university.exam.userManagement.services.AdminService;
import com.university.exam.userManagement.services.StudentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {
    
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final AdminService adminService;
    private final StudentService studentService;

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        CustomUserDetails userDetails = (CustomUserDetails) userRepository.findByEmail(request.getEmail())
                .map(CustomUserDetails::new)
                .orElseThrow(() -> new RuntimeException("User not found"));

        var jwtToken = jwtService.generateToken(userDetails);
        var refreshToken = jwtService.generateRefreshToken(userDetails);
        var email = userDetails.getUsername();

        // Build authentication response based on user type
        try {
            UUID userId = userDetails.getUser().getUserId();

            return switch (userDetails.getUserType().toUpperCase()) {
                case "SUPERADMIN", "ADMIN", "VIEWER" -> {
                    AdminResponseDTO adminData = adminService.getAdminByUserId(userId);
                    yield AuthenticationResponseBuilder.buildAdminResponse(
                            jwtToken, refreshToken, adminData);
                }
                case "STUDENT" -> {
                    StudentResponseDTO studentData = studentService.getStudentByUserId(userId);
                    yield AuthenticationResponseBuilder.buildStudentResponse(
                            jwtToken, refreshToken, studentData);
                }
                default -> {
                    log.warn("Unknown user type: {}", userDetails.getUserType());
                    throw new Exception();
                }
            };
        } catch (Exception e) {
            log.error("Error fetching user-specific data for user: {}", email, e);
            // Fallback to basic response without user-specific data
            throw new ValidationException("Error fetching user-specific data for user : " + email);
        }
    }
}