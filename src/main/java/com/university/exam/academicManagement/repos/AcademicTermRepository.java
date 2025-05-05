package com.university.exam.academicManagement.repos;

import com.university.exam.academicManagement.entities.AcademicTerm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AcademicTermRepository extends JpaRepository<AcademicTerm, UUID> {
    Optional<AcademicTerm> findByIdAndAcademicYearId(UUID termId, UUID academicYearId);
    List<AcademicTerm> findByAcademicYearId(UUID academicYearId);
}