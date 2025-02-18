package com.university.exam.dtos.responseDTO;

import com.university.exam.entities.Course;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

@Data
public class CourseResponseDTO {
    private String code;
    private String name;
    private UUID avatarId;
    private String avatar;
    private boolean active;
    private UUID groupId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UUID baseDirectoryId;

    public static CourseResponseDTO fromEntity(Course course, byte[] avatar, String avatarType) {
        CourseResponseDTO dto = new CourseResponseDTO();
        dto.setCode(course.getCode());
        dto.setName(course.getName());
        dto.setAvatarId(course.getAvatarId());
        if (avatar != null && avatarType != null) {
            String base64Image = Base64.getEncoder().encodeToString(avatar);
            dto.setAvatar("data:" + avatarType + ";base64," + base64Image);
        }
        dto.setActive(course.isActive());
        dto.setGroupId(course.getGroup().getId());
        dto.setCreatedAt(course.getCreatedAt());
        dto.setUpdatedAt(course.getUpdatedAt());
        dto.setBaseDirectoryId(course.getBaseDirectory().getId());
        return dto;
    }
}
