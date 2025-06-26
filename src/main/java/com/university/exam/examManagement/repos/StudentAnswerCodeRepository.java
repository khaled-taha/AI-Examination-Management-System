package com.university.exam.examManagement.repos;

import com.university.exam.examManagement.entities.StudentAnswerCode;
import com.university.exam.examManagement.entities.StudentExamAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StudentAnswerCodeRepository extends JpaRepository<StudentAnswerCode, UUID> {

    @Query("SELECT COUNT(a) > 0 FROM StudentAnswerCode a WHERE a.examQuestion.id = :questionId")
    boolean existsByQuestionId(UUID questionId);

    List<StudentAnswerCode> findByStudentExamAttempt(StudentExamAttempt attempt);

    Optional<StudentAnswerCode> findByStudentExamAttemptIdAndExamQuestionId(UUID attemptId, UUID questionId);
}