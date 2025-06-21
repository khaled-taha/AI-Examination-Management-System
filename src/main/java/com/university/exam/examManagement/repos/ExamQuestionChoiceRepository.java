package com.university.exam.examManagement.repos;

import com.university.exam.examManagement.entities.ExamQuestionChoice;
import com.university.exam.examManagement.entities.ExamQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface ExamQuestionChoiceRepository extends JpaRepository<ExamQuestionChoice, UUID> {
    List<ExamQuestionChoice> findByExamQuestion(ExamQuestion examQuestion);
    
    // Convenience method for backward compatibility
    default List<ExamQuestionChoice> findByExamQuestionId(UUID examQuestionId) {
        return findByExamQuestion(new ExamQuestion() {{ setId(examQuestionId); }});
    }
} 