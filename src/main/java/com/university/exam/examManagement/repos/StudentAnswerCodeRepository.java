package com.university.exam.examManagement.repos;

import com.university.exam.examManagement.entities.StudentAnswerCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface StudentAnswerCodeRepository extends JpaRepository<StudentAnswerCode, UUID> {

    @Query("SELECT COUNT(a) > 0 FROM StudentAnswerCode a WHERE a.examQuestion.id = :questionId")
    boolean existsByQuestionId(UUID questionId);
} 