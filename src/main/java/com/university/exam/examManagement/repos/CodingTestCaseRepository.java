package com.university.exam.examManagement.repos;

import com.university.exam.examManagement.entities.CodingTestCase;
import com.university.exam.examManagement.entities.ExamQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface CodingTestCaseRepository extends JpaRepository<CodingTestCase, UUID> {
    List<CodingTestCase> findByExamQuestion(ExamQuestion examQuestion);
    List<CodingTestCase> findByExamQuestionAndIsSample(ExamQuestion examQuestion, boolean isSample);
    
    // Convenience method for backward compatibility
    default List<CodingTestCase> findByExamQuestionId(UUID examQuestionId) {
        return findByExamQuestion(new ExamQuestion() {{ setId(examQuestionId); }});
    }
} 