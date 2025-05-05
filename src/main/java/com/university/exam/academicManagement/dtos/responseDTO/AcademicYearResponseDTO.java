package com.university.exam.academicManagement.dtos.responseDTO;

import com.university.exam.academicManagement.entities.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
public class AcademicYearResponseDTO {
    private UUID id;
    private String year;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<TermResponse> terms;
    private List<CourseResponse> courses;

    @Data
    public static class TermResponse {
        private UUID id;
        private String name;
        private Integer termOrder;
        private LocalDate startDate;
        private LocalDate endDate;
        private String status;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public static TermResponse fromEntity(AcademicTerm term) {
            TermResponse response = new TermResponse();
            response.setId(term.getId());
            response.setName(term.getName());
            response.setTermOrder(term.getTermOrder());
            response.setStartDate(term.getStartDate());
            response.setEndDate(term.getEndDate());
            response.setStatus(term.getStatus());
            response.setCreatedAt(term.getCreatedAt());
            response.setUpdatedAt(term.getUpdatedAt());
            return response;
        }
    }

    @Data
    public static class CourseResponse {
        private UUID id;
        private String courseCode;
        private UUID termId;
        private String termName;
        private List<CourseAdminResponse> admins;

        public static CourseResponse fromEntity(AcademicYearCourse course, List<AcademicYearCourseAdmin> admins) {
            CourseResponse response = new CourseResponse();
            response.setId(course.getId());
            response.setCourseCode(course.getCourseCode());
            response.setTermId(course.getTerm().getId());
            response.setTermName(course.getTerm().getName());
            response.setAdmins(admins.stream()
                    .map(CourseAdminResponse::fromEntity)
                    .collect(Collectors.toList()));
            return response;
        }
    }

    @Data
    public static class CourseAdminResponse {
        private UUID id;
        private UUID adminId;
        private String adminName;

        public static CourseAdminResponse fromEntity(AcademicYearCourseAdmin admin) {
            CourseAdminResponse response = new CourseAdminResponse();
            response.setId(admin.getId());
            response.setAdminId(admin.getAdmin().getAdminId());
            response.setAdminName(admin.getAdmin().getUser().getFullName());
            return response;
        }
    }

    public static AcademicYearResponseDTO fromEntity(
            AcademicYear academicYear,
            List<AcademicTerm> terms,
            List<AcademicYearCourse> courses,
            Map<UUID, List<AcademicYearCourseAdmin>> adminsByCourseId
    ) {
        AcademicYearResponseDTO response = new AcademicYearResponseDTO();
        response.setId(academicYear.getId());
        response.setYear(academicYear.getYear());
        response.setStartDate(academicYear.getStartDate());
        response.setEndDate(academicYear.getEndDate());
        response.setCreatedAt(academicYear.getCreatedAt());
        response.setUpdatedAt(academicYear.getUpdatedAt());

        // Map terms
        response.setTerms(terms.stream()
                .map(TermResponse::fromEntity)
                .collect(Collectors.toList()));

        // Map courses with their admins
        response.setCourses(courses.stream()
                .map(course -> CourseResponse.fromEntity(
                        course,
                        adminsByCourseId.getOrDefault(course.getId(), List.of())
                ))
                .collect(Collectors.toList()));

        return response;
    }

    public static AcademicYearResponseDTO fromEntity(AcademicYear academicYear, List<AcademicTerm> terms) {
        return fromEntity(academicYear, terms, List.of(), Map.of());
    }
    

    @Data
    public static class SaveAcademicYearTermsResponseDTO {
        private UUID id;
        private String year;
        private LocalDate startDate;
        private LocalDate endDate;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private List<TermResponse> terms;

        public static SaveAcademicYearTermsResponseDTO fromEntity(AcademicYear academicYear, List<AcademicTerm> terms) {
            SaveAcademicYearTermsResponseDTO response = new SaveAcademicYearTermsResponseDTO();
            response.setId(academicYear.getId());
            response.setYear(academicYear.getYear());
            response.setStartDate(academicYear.getStartDate());
            response.setEndDate(academicYear.getEndDate());
            response.setCreatedAt(academicYear.getCreatedAt());
            response.setUpdatedAt(academicYear.getUpdatedAt());
            response.setTerms(terms.stream()
                    .map(TermResponse::fromEntity)
                    .collect(Collectors.toList()));
            return response;
        }
    }
    @Data
    public static class SaveYearResponse {
        private UUID id;
        private String year;
        private String groupName;
        private LocalDate startDate;
        private LocalDate endDate;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public static AcademicYearResponseDTO.SaveYearResponse fromEntity(AcademicYear academicYear, AcademicYearGroup academicYearGroup) {
            AcademicYearResponseDTO.SaveYearResponse response = new AcademicYearResponseDTO.SaveYearResponse();
            response.setId(academicYear.getId());
            response.setYear(academicYear.getYear());
            response.setGroupName(academicYearGroup.getGroup().getName());
            response.setStartDate(academicYear.getStartDate());
            response.setEndDate(academicYear.getEndDate());
            response.setCreatedAt(academicYear.getCreatedAt());
            response.setUpdatedAt(academicYear.getUpdatedAt());
            return response;
        }
    }
} 