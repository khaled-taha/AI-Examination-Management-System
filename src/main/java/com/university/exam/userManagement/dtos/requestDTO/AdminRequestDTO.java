package com.university.exam.userManagement.dtos.requestDTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.UUID;

@Data
public class AdminRequestDTO {
    private UserRequestDTO userRequestDTO;

    @NotBlank(message = "Specialization ID is required")
    private UUID specializationId;
}
