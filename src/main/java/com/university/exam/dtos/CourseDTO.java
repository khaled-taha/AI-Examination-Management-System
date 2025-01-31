package com.university.exam.dtos;

import com.university.exam.entities.Course;
import lombok.Data;

import jakarta.validation.constraints.*;
import java.util.UUID;

@Data
public class CourseDTO {
    @NotBlank(message = "Course code is required")
    @Size(min = 5, max = 10, message = "Course code must be between 5 and 10 characters")
    private String code;

    @NotBlank(message = "Course name is required")
    @Size(max = 100, message = "Course name cannot exceed 100 characters")
    private String name;

    @NotNull(message = "Group ID is required")
    private UUID groupId;

    private boolean active = true;

    private byte[] avatar;
    private String avatarType;

    @NotNull(message = "Base Directory ID is required")
    private UUID baseDirectoryId;

    public static CourseDTO fromEntity(Course course, byte[] avatar, String avatarType) {
        CourseDTO dto = new CourseDTO();
        dto.setCode(course.getCode());
        dto.setName(course.getName());
        dto.setAvatar(avatar);
        dto.setAvatarType(avatarType);
        dto.setActive(course.isActive());
        dto.setGroupId(course.getGroup().getId());
        dto.setBaseDirectoryId(course.getBaseDirectory().getId());
        return dto;
    }
}