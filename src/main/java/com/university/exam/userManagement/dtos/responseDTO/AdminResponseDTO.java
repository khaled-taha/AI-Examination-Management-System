package com.university.exam.userManagement.dtos.responseDTO;

import com.university.exam.courseManagement.dtos.responseDTO.GroupResponseDTO;
import com.university.exam.userManagement.entities.Admin;
import lombok.Data;

import java.util.List;

@Data
public class AdminResponseDTO {
    private UserResponseDTO userResponseDTO;
    private SpecializationResponseDTO specializationResponseDTO;

    public static AdminResponseDTO fromEntity(Admin admin) {
        AdminResponseDTO responseDTO = new AdminResponseDTO();
        responseDTO.setUserResponseDTO(UserResponseDTO.convertToUserResponseDTO(admin.getUser()));
        responseDTO.setSpecializationResponseDTO(SpecializationResponseDTO.convertToSpecializationResponseDTO(admin.getSpecialization()));
        return responseDTO;
    }
}
