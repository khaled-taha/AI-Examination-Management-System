package com.university.exam.academicManagement.repos;

import com.university.exam.academicManagement.entities.AcademicYearCourseAdmin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AcademicYearCourseAdminRepository extends JpaRepository<AcademicYearCourseAdmin, UUID> {
    List<AcademicYearCourseAdmin> findByAcademicYearCourseId(UUID academicYearCourseId);
    List<AcademicYearCourseAdmin> findByAcademicYearCourseIdIn(List<UUID> academicYearCourseIds);
    List<AcademicYearCourseAdmin> findByAdmin_AdminId(UUID adminId);

    @Query("""
    SELECT CASE WHEN COUNT(ayca) > 0 THEN TRUE ELSE FALSE END
    FROM AcademicYearCourseAdmin ayca
    WHERE ayca.academicYearCourse.id = :academicYearCourseId
      AND ayca.admin.adminId = :adminId
""")
    boolean existsByAcademicYearCourseIdAndAdminId(@Param("academicYearCourseId") UUID academicYearCourseId,
                                                   @Param("adminId") UUID adminId);
} 