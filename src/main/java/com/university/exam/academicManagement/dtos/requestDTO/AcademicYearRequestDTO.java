package com.university.exam.academicManagement.dtos.requestDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
public class AcademicYearRequestDTO {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SaveYearRequest {
        private UUID id;
        private String year;
        private UUID groupId;
        private LocalDate startDate;
        private LocalDate endDate;
        private boolean isActive;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SaveTermsRequest {
        private UUID academicYearId;
        private List<TermDTO> terms;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class TermDTO {
            private UUID termId;
            private String name;
            private Integer termOrder;
            private LocalDate startDate;
            private LocalDate endDate;
        }
    }
} 