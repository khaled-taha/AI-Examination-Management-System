package com.university.exam.examManagement.repos;

import com.university.exam.examManagement.entities.ExamQuestionChoice;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ExamQuestionChoiceRepository extends JpaRepository<ExamQuestionChoice, UUID> {
} 