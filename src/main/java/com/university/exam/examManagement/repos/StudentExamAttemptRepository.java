package com.university.exam.examManagement.repos;

import com.university.exam.examManagement.entities.StudentExamAttempt;
import com.university.exam.examManagement.entities.Exam;
import com.university.exam.userManagement.entities.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StudentExamAttemptRepository extends JpaRepository<StudentExamAttempt, UUID> {
    List<StudentExamAttempt> findByExam(Exam exam);
    List<StudentExamAttempt> findByStudentAndExam(Student student, Exam exam);
    List<StudentExamAttempt> findByStudent(Student student);
    
    @Query("SELECT sea FROM StudentExamAttempt sea WHERE sea.student.id = :studentId AND sea.exam.id = :examId ORDER BY sea.attemptNumber DESC LIMIT 1")
    Optional<StudentExamAttempt> findLatestAttemptByStudentAndExam(@Param("studentId") UUID studentId, @Param("examId") UUID examId);
    
    // Convenience methods for backward compatibility
    default List<StudentExamAttempt> findByExamId(UUID examId) {
        return findByExam(new Exam() {{ setId(examId); }});
    }
    
    default List<StudentExamAttempt> findByStudentIdAndExamId(UUID studentId, UUID examId) {
        return findByStudentAndExam(new Student() {{ setStudentId(studentId); }}, new Exam() {{ setId(examId); }});
    }
    
    default List<StudentExamAttempt> findByStudentId(UUID studentId) {
        return findByStudent(new Student() {{ setStudentId(studentId); }});
    }
} 