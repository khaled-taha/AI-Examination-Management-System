package com.university.exam.dtos.responseDTO;

import com.university.exam.entities.Course;
import com.university.exam.entities.Group;
import lombok.Data;

import java.util.UUID;

@Data
public class GroupResponseDTO {
    private UUID id;
    private String name;
    private String description;

    public static GroupResponseDTO fromEntity(Group group) {
        GroupResponseDTO dto = new GroupResponseDTO();
        dto.setId(group.getId());
        dto.setName(group.getName());
        dto.setDescription(group.getDescription());
        return dto;
    }
}
