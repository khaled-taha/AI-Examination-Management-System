package com.university.exam.academicManagement.dtos.responseDTO;

import com.university.exam.academicManagement.entities.AcademicYear;
import com.university.exam.academicManagement.entities.AcademicYearGroup;
import com.university.exam.courseManagement.entities.Group;
import lombok.Data;

import java.util.UUID;

@Data
public class AcademicYearGroupResponseDTO {
    private UUID id;
    private AcademicYear academicYear;
    private Group group;

    public AcademicYearGroupResponseDTO() {
    }

    public AcademicYearGroupResponseDTO(UUID id, AcademicYear academicYear, Group group) {
        this.id = id;
        this.academicYear = academicYear;
        this.group = group;
    }

    public static AcademicYearGroupResponseDTO fromEntity(AcademicYearGroup group) {
        return new AcademicYearGroupResponseDTO(group.getId(),group.getAcademicYear(),group.getGroup());
    }
}
