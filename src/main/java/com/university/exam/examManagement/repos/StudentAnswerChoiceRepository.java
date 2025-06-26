package com.university.exam.examManagement.repos;

import com.university.exam.examManagement.entities.StudentAnswerChoice;
import com.university.exam.examManagement.entities.StudentExamAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StudentAnswerChoiceRepository extends JpaRepository<StudentAnswerChoice, UUID> {

    @Query("SELECT COUNT(a) > 0 FROM StudentAnswerChoice a WHERE a.examQuestion.id = :questionId")
    boolean existsByQuestionId(UUID questionId);

    List<StudentAnswerChoice> findByStudentExamAttempt(StudentExamAttempt attempt);

    Optional<StudentAnswerChoice> findByStudentExamAttemptIdAndExamQuestionId(UUID attemptId, UUID questionId);
}