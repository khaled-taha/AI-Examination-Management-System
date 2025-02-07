package com.university.exam.courseManagement.dtos.responseDTO;

import lombok.Data;

import java.util.UUID;

@Data
public class GroupResponseDTO {
    private UUID id;
    private String name;
    private String description;
}
