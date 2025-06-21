package com.university.exam.examManagement.repos;

import com.university.exam.examManagement.entities.ExamSection;
import com.university.exam.examManagement.entities.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface ExamSectionRepository extends JpaRepository<ExamSection, UUID> {
    List<ExamSection> findByExam(Exam exam);
    
    // Convenience method for backward compatibility
    default List<ExamSection> findByExamId(UUID examId) {
        return findByExam(new Exam() {{ setId(examId); }});
    }
} 