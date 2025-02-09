package com.university.exam.userManagement.dtos.responseDTO;

import com.university.exam.userManagement.entities.ClassStudy;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ClassStudyResponseDTO {
    private UUID classId;
    private Integer year;
    private BigDecimal totalGrade;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ClassStudyResponseDTO convertToClassStudyResponseDTO(ClassStudy classStudy) {
        ClassStudyResponseDTO responseDTO = new ClassStudyResponseDTO();
        responseDTO.setClassId(classStudy.getClassId());
        responseDTO.setYear(classStudy.getYear());
        responseDTO.setTotalGrade(classStudy.getTotalGrade());
        responseDTO.setCreatedAt(classStudy.getCreatedAt());
        responseDTO.setUpdatedAt(classStudy.getUpdatedAt());
        return responseDTO;
    }
}
