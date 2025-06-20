package com.university.exam.academicManagement.repos;

import com.university.exam.academicManagement.entities.AcademicYear;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AcademicYearRepository extends JpaRepository<AcademicYear, UUID> {
    @Query("SELECT COUNT(a) > 0 FROM AcademicYear a WHERE a.year = :year")
    boolean existsByYear(String year);
}