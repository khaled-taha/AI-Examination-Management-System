package com.university.exam.examManagement.repos;

import com.university.exam.examManagement.entities.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ExamRepository extends JpaRepository<Exam, UUID> {
} 