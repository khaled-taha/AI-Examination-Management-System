package com.university.exam.security.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.university.exam.userManagement.dtos.responseDTO.AdminResponseDTO;
import com.university.exam.userManagement.dtos.responseDTO.StudentResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthenticationResponse {
    private String token;
    private String refreshToken;
    
    // Flexible user data based on user type with proper type information
    @JsonProperty("userData")
    @JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
    )
    @JsonSubTypes({
        @JsonSubTypes.Type(value = AdminResponseDTO.class, name = "ADMIN"),
        @JsonSubTypes.Type(value = StudentResponseDTO.class, name = "STUDENT")
    })
    private Object userData;
    
    // Helper methods to set user data based on type
    public void setAdminData(AdminResponseDTO adminData) {
        this.userData = adminData;
    }
    
    public void setStudentData(StudentResponseDTO studentData) {
        this.userData = studentData;
    }
    
    // Helper methods to get typed data
    @SuppressWarnings("unchecked")
    public AdminResponseDTO getAdminData() {
        return userData instanceof AdminResponseDTO ? (AdminResponseDTO) userData : null;
    }
    
    @SuppressWarnings("unchecked")
    public StudentResponseDTO getStudentData() {
        return userData instanceof StudentResponseDTO ? (StudentResponseDTO) userData : null;
    }
    
    // Static builder methods for different user types
    public static AuthenticationResponseBuilder adminBuilder() {
        return builder();
    }
    
    public static AuthenticationResponseBuilder studentBuilder() {
        return builder();
    }
    
    // Convenience method to check if user data is present
    public boolean hasUserData() {
        return userData != null;
    }
    
    // Convenience method to get user data type
    public String getUserDataType() {
        if (userData instanceof AdminResponseDTO) {
            return "ADMIN";
        } else if (userData instanceof StudentResponseDTO) {
            return "STUDENT";
        }
        return null;
    }
} 