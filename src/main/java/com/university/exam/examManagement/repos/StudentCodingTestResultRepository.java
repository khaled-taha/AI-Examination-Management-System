package com.university.exam.examManagement.repos;

import com.university.exam.examManagement.entities.StudentCodingTestResult;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface StudentCodingTestResultRepository extends JpaRepository<StudentCodingTestResult, UUID> {
} 