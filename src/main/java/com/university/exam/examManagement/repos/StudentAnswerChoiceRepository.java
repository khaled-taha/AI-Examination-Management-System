package com.university.exam.examManagement.repos;

import com.university.exam.examManagement.entities.StudentAnswerChoice;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface StudentAnswerChoiceRepository extends JpaRepository<StudentAnswerChoice, UUID> {
} 