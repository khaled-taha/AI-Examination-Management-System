package com.university.exam.examManagement.repos;

import com.university.exam.examManagement.entities.Exam;
import com.university.exam.academicManagement.entities.AcademicTerm;
import com.university.exam.academicManagement.entities.AcademicYearGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.UUID;

public interface ExamRepository extends JpaRepository<Exam, UUID> {
    List<Exam> findByAcademicYearGroup(AcademicYearGroup academicYearGroup);
    List<Exam> findByAcademicYearGroupAndTerm(AcademicYearGroup academicYearGroup, AcademicTerm term);
    
    @Query("SELECT e FROM Exam e WHERE e.academicYearGroup.id = :academicYearGroupId AND e.term.id = :termId ORDER BY e.createdAt DESC")
    List<Exam> findExamsByAcademicYearAndTerm(@Param("academicYearGroupId") UUID academicYearGroupId, @Param("termId") UUID termId);
} 