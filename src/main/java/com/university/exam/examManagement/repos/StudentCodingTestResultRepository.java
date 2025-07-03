package com.university.exam.examManagement.repos;

import com.university.exam.examManagement.entities.StudentCodingTestResult;
import com.university.exam.examManagement.entities.StudentAnswerCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

public interface StudentCodingTestResultRepository extends JpaRepository<StudentCodingTestResult, UUID> {
    List<StudentCodingTestResult> findByStudentAnswerCode(StudentAnswerCode studentAnswerCode);
    
    // Convenience method for backward compatibility
    default List<StudentCodingTestResult> findByStudentAnswerCodeId(UUID studentAnswerCodeId) {
        return findByStudentAnswerCode(new StudentAnswerCode() {{ setId(studentAnswerCodeId); }});
    }
    
    // Delete all results for a specific student answer code (for overriding previous results)
    @Modifying
    @Transactional
    @Query("DELETE FROM StudentCodingTestResult r WHERE r.studentAnswerCode.id = :studentAnswerCodeId")
    void deleteByStudentAnswerCodeId(@Param("studentAnswerCodeId") UUID studentAnswerCodeId);
} 