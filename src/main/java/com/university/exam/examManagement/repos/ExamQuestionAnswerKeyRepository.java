package com.university.exam.examManagement.repos;

import com.university.exam.examManagement.entities.ExamQuestionAnswerKey;
import com.university.exam.examManagement.entities.ExamQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface ExamQuestionAnswerKeyRepository extends JpaRepository<ExamQuestionAnswerKey, UUID> {
    List<ExamQuestionAnswerKey> findByExamQuestion(ExamQuestion examQuestion);
    
    // Convenience method for backward compatibility
    default List<ExamQuestionAnswerKey> findByExamQuestionId(UUID examQuestionId) {
        return findByExamQuestion(new ExamQuestion() {{ setId(examQuestionId); }});
    }

    @Modifying
    @Transactional
    @Query("DELETE FROM ExamQuestionAnswerKey a WHERE a.examQuestion.id = :questionId")
    void deleteByQuestionId(@Param("questionId") UUID questionId);
} 