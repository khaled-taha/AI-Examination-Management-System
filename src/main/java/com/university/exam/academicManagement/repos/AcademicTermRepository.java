package com.university.exam.academicManagement.repos;

import com.university.exam.academicManagement.entities.AcademicTerm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AcademicTermRepository extends JpaRepository<AcademicTerm, UUID> {
    List<AcademicTerm> findByAcademicYearId(UUID academicYearId);
    Optional<AcademicTerm> findByAcademicYearIdAndTermOrder(UUID academicYearId, Integer termOrder);
}