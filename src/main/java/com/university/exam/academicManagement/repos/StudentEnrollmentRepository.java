package com.university.exam.academicManagement.repos;

import com.university.exam.academicManagement.entities.StudentEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface StudentEnrollmentRepository extends JpaRepository<StudentEnrollment, UUID> {
    Optional<StudentEnrollment> findByStudent_StudentId(UUID studentId);
}
