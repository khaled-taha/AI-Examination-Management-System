package com.university.exam.userManagement.dtos.responseDTO;

import com.university.exam.userManagement.entities.Admin;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class AdminResponseDTO {
    private UserResponseDTO userResponseDTO;
    private SpecializationResponseDTO specializationResponseDTO;

    public static AdminResponseDTO convertToAdminResponseDTO(Admin admin) {
        AdminResponseDTO responseDTO = new AdminResponseDTO();
        responseDTO.setUserResponseDTO(UserResponseDTO.convertToUserResponseDTO(admin.getUser()));
        responseDTO.setSpecializationResponseDTO(SpecializationResponseDTO.convertToSpecializationResponseDTO(admin.getSpecialization()));
        return responseDTO;
    }
}
