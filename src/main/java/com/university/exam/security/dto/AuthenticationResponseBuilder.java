package com.university.exam.security.dto;

import com.university.exam.userManagement.dtos.responseDTO.AdminResponseDTO;
import com.university.exam.userManagement.dtos.responseDTO.StudentResponseDTO;
import lombok.experimental.UtilityClass;

/**
 * Utility class to help build AuthenticationResponse objects with appropriate user data
 */
@UtilityClass
public class AuthenticationResponseBuilder {
    
    /**
     * Build authentication response for admin users
     */
    public static AuthenticationResponse buildAdminResponse(
            String token, 
            String refreshToken,
            AdminResponseDTO adminData) {
        
        return AuthenticationResponse.builder()
                .token(token)
                .refreshToken(refreshToken)
                .userData(adminData)
                .build();
    }
    
    /**
     * Build authentication response for student users
     */
    public static AuthenticationResponse buildStudentResponse(
            String token, 
            String refreshToken,
            StudentResponseDTO studentData) {
        
        return AuthenticationResponse.builder()
                .token(token)
                .refreshToken(refreshToken)
                .userData(studentData)
                .build();
    }
} 