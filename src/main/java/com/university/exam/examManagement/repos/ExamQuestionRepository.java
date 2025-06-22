package com.university.exam.examManagement.repos;

import com.university.exam.examManagement.entities.ExamQuestion;
import com.university.exam.examManagement.entities.Exam;
import com.university.exam.examManagement.entities.ExamSection;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface ExamQuestionRepository extends JpaRepository<ExamQuestion, UUID> {
    List<ExamQuestion> findByExam(Exam exam);
    List<ExamQuestion> findBySection(ExamSection section);
    
    // Convenience method for backward compatibility
    default List<ExamQuestion> findByExamId(UUID examId) {
        return findByExam(new Exam() {{ setId(examId); }});
    }
} 