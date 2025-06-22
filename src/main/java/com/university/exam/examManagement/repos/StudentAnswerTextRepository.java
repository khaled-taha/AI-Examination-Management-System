package com.university.exam.examManagement.repos;

import com.university.exam.examManagement.entities.StudentAnswerText;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface StudentAnswerTextRepository extends JpaRepository<StudentAnswerText, UUID> {

    @Query("SELECT COUNT(a) > 0 FROM StudentAnswerText a WHERE a.examQuestion.id = :questionId")
    boolean existsByQuestionId(UUID questionId);
} 