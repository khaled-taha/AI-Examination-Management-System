package com.university.exam.examManagement.repos;

import com.university.exam.examManagement.entities.StudentAnswerChoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface StudentAnswerChoiceRepository extends JpaRepository<StudentAnswerChoice, UUID> {

    @Query("SELECT COUNT(a) > 0 FROM StudentAnswerChoice a WHERE a.examQuestion.id = :questionId")
    boolean existsByQuestionId(UUID questionId);
} 