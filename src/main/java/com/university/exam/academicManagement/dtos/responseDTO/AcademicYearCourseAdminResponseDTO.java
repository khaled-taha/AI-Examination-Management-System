package com.university.exam.academicManagement.dtos.responseDTO;

import com.university.exam.academicManagement.entities.AcademicYearCourseAdmin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

public class AcademicYearCourseAdminResponseDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CourseAdminResponse {
        private UUID academicYearCourseAdminId;
        private UUID academicYearCourseId;
        private String courseCode;
        private UUID termId;
        private String termName;
        private UUID adminId;
        private String adminName;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public static AcademicYearCourseAdminResponseDTO.CourseAdminResponse fromEntity(AcademicYearCourseAdmin entity) {
            AcademicYearCourseAdminResponseDTO.CourseAdminResponse response = new AcademicYearCourseAdminResponseDTO.CourseAdminResponse();
            response.setAcademicYearCourseAdminId(entity.getId());
            response.setAcademicYearCourseId(entity.getAcademicYearCourse().getId());
            response.setCourseCode(entity.getAcademicYearCourse().getCourseCode());
            response.setTermId(entity.getAcademicYearCourse().getTerm().getId());
            response.setTermName(entity.getAcademicYearCourse().getTerm().getName());
            response.setAdminId(entity.getAdmin().getAdminId());
            response.setAdminName(entity.getAdmin().getUser().getFullName());
            response.setCreatedAt(entity.getCreatedAt());
            response.setUpdatedAt(entity.getUpdatedAt());
            return response;
        }
    }
}
