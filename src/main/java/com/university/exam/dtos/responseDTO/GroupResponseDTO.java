package com.university.exam.dtos.responseDTO;

import lombok.Data;

import java.util.UUID;

@Data
public class GroupResponseDTO {
    private UUID id;
    private String name;
    private String description;
}
