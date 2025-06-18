package com.university.exam.examManagement.repos;

import com.university.exam.examManagement.entities.StudentAnswerText;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface StudentAnswerTextRepository extends JpaRepository<StudentAnswerText, UUID> {
} 