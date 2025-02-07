package com.university.exam.resourceManagement.dtos.requestDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.UUID;

@Data
public class ResourceRequestDTO {
    @NotBlank(message = "Resource name is required")
    @Size(max = 100, message = "Resource name cannot exceed 100 characters")
    private String name;

    @NotBlank(message = "Resource type is required")
    @Size(max = 50, message = "Resource type cannot exceed 50 characters")
    private String type;

    @NotNull(message = "Resource directory ID is required")
    private UUID resourceDirId;
}
