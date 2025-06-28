package com.university.exam.academicManagement.dtos.requestDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

public class AcademicYearCourseAdminRequestDTO {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AssignAdminRequest {
        private UUID academicYearCourseId;
        private UUID userId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RemoveAdminRequest {
        private UUID academicYearCourseId;
        private UUID adminId;
    }
}
