package com.university.exam.examManagement.repos;

import com.university.exam.examManagement.entities.ExamQuestionAnswerKey;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ExamQuestionAnswerKeyRepository extends JpaRepository<ExamQuestionAnswerKey, UUID> {
} 