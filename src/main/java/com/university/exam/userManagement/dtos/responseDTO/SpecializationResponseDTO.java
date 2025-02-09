package com.university.exam.userManagement.dtos.responseDTO;

import com.university.exam.userManagement.entities.Specialization;
import com.university.exam.userManagement.entities.User;
import lombok.Data;

import java.util.UUID;

@Data
public class SpecializationResponseDTO {
    private UUID specializationId;
    private String specializationName;

    public static SpecializationResponseDTO convertToSpecializationResponseDTO(Specialization specialization) {
        SpecializationResponseDTO specializationResponseDTO = new SpecializationResponseDTO();
        specializationResponseDTO.setSpecializationId(specialization.getSpecializationId());
        specializationResponseDTO.setSpecializationName(specializationResponseDTO.getSpecializationName());
        return specializationResponseDTO;
    }
}
