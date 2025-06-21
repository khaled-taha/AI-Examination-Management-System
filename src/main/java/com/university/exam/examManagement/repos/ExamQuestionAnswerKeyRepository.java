package com.university.exam.examManagement.repos;

import com.university.exam.examManagement.entities.ExamQuestionAnswerKey;
import com.university.exam.examManagement.entities.ExamQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface ExamQuestionAnswerKeyRepository extends JpaRepository<ExamQuestionAnswerKey, UUID> {
    List<ExamQuestionAnswerKey> findByExamQuestion(ExamQuestion examQuestion);
    
    // Convenience method for backward compatibility
    default List<ExamQuestionAnswerKey> findByExamQuestionId(UUID examQuestionId) {
        return findByExamQuestion(new ExamQuestion() {{ setId(examQuestionId); }});
    }
} 