package com.university.exam.academicManagement.repos;

import com.university.exam.academicManagement.entities.StudentEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StudentEnrollmentRepository extends JpaRepository<StudentEnrollment, UUID> {
    Optional<StudentEnrollment> findByStudent_StudentId(UUID studentId);

    @Query("SELECT e FROM StudentEnrollment e WHERE e.student.studentId = :studentId ORDER BY e.term.startDate DESC")
    Optional<StudentEnrollment> findLatestByStudentId(@Param("studentId") UUID studentId);

    @Query("""
    SELECT COUNT(e) > 0 FROM StudentEnrollment e
    WHERE e.student.studentId = :studentId
      AND e.academicYearGroup.id = :academicYearGroupId
      AND e.term.id = :termId
      AND e.enrollmentStatus = 'ACTIVE'
""")
    boolean isStudentEnrolledInYearGroupAndTerm(
            @Param("studentId") UUID studentId,
            @Param("academicYearGroupId") UUID academicYearGroupId,
            @Param("termId") UUID termId
    );

    List<StudentEnrollment> findByAcademicYearGroup_AcademicYear_IdAndEnrollmentStatus(UUID academicYearId, StudentEnrollment.EnrollmentStatus status);
}
