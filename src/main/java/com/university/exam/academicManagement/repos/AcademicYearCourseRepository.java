package com.university.exam.academicManagement.repos;

import com.university.exam.academicManagement.entities.AcademicYearCourse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AcademicYearCourseRepository extends JpaRepository<AcademicYearCourse, UUID> {
    List<AcademicYearCourse> findByAcademicYearId(UUID academicYearId);
    void deleteByAcademicYearId(UUID academicYearId);
}
