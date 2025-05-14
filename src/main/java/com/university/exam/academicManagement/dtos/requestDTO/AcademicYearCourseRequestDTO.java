package com.university.exam.academicManagement.dtos.requestDTO;

import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
public class AcademicYearCourseRequestDTO {
    private UUID termId;
    private List<String> courseCodes;
} 