package com.university.exam.userManagement.dtos.responseDTO;

import com.university.exam.courseManagement.dtos.responseDTO.GroupResponseDTO;
import com.university.exam.userManagement.entities.Admin;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class AdminResponseDTO {
    private UserResponseDTO userResponseDTO;
    private UUID adminId;
    private SpecializationResponseDTO specializationResponseDTO;

    public static AdminResponseDTO fromEntity(Admin admin) {
        AdminResponseDTO responseDTO = new AdminResponseDTO();
        responseDTO.setUserResponseDTO(UserResponseDTO.convertToUserResponseDTO(admin.getUser()));
        responseDTO.setAdminId(admin.getAdminId());
        responseDTO.setSpecializationResponseDTO(SpecializationResponseDTO.convertToSpecializationResponseDTO(admin.getSpecialization()));
        return responseDTO;
    }
}
