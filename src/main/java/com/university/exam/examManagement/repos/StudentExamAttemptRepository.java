package com.university.exam.examManagement.repos;

import com.university.exam.examManagement.entities.StudentExamAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface StudentExamAttemptRepository extends JpaRepository<StudentExamAttempt, UUID> {
} 