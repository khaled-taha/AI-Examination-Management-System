package com.university.exam.academicManagement.dtos.responseDTO;

import com.university.exam.academicManagement.entities.AcademicYearCourse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AcademicYearCourseResponseDTO {
    private UUID academicYearCourseId;
    private UUID academicYearId;
    private String academicYear;
    private String courseCode;
    private UUID termId;
    private String termName;
    private Integer termOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static AcademicYearCourseResponseDTO fromEntity(AcademicYearCourse entity) {
        AcademicYearCourseResponseDTO response = new AcademicYearCourseResponseDTO();
        response.setAcademicYearCourseId(entity.getId());
        response.setAcademicYearId(entity.getAcademicYear().getId());
        response.setAcademicYear(entity.getAcademicYear().getYear());
        response.setCourseCode(entity.getCourseCode());
        response.setTermId(entity.getTerm().getId());
        response.setTermName(entity.getTerm().getName());
        response.setTermOrder(entity.getTerm().getTermOrder());
        response.setCreatedAt(entity.getCreatedAt());
        response.setUpdatedAt(entity.getUpdatedAt());
        return response;
    }
}
