package com.university.exam.academicManagement.repos;

import com.university.exam.academicManagement.entities.AcademicYearCourseAdmin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AcademicYearCourseAdminRepository extends JpaRepository<AcademicYearCourseAdmin, UUID> {
    List<AcademicYearCourseAdmin> findByAcademicYearCourseId(UUID academicYearCourseId);
    List<AcademicYearCourseAdmin> findByAcademicYearCourseIdIn(List<UUID> academicYearCourseIds);
    List<AcademicYearCourseAdmin> findByAdmin_AdminId(UUID adminId);
    boolean existsByAcademicYearCourseIdAndAdmin_AdminId(UUID academicYearCourseId, UUID adminId);
} 