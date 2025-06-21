package com.university.exam.examManagement.repos;

import com.university.exam.examManagement.entities.StudentCodingTestResult;
import com.university.exam.examManagement.entities.StudentAnswerCode;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface StudentCodingTestResultRepository extends JpaRepository<StudentCodingTestResult, UUID> {
    List<StudentCodingTestResult> findByStudentAnswerCode(StudentAnswerCode studentAnswerCode);
    
    // Convenience method for backward compatibility
    default List<StudentCodingTestResult> findByStudentAnswerCodeId(UUID studentAnswerCodeId) {
        return findByStudentAnswerCode(new StudentAnswerCode() {{ setId(studentAnswerCodeId); }});
    }
} 