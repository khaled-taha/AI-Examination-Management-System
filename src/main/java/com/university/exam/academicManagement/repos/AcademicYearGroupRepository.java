package com.university.exam.academicManagement.repos;

import com.university.exam.academicManagement.entities.AcademicYear;
import com.university.exam.academicManagement.entities.AcademicYearGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AcademicYearGroupRepository extends JpaRepository<AcademicYearGroup, UUID> {
    List<AcademicYear> findByGroupId(UUID groupId);
    Optional<AcademicYearGroup> findByAcademicYearIdAndGroupId(UUID academicYearId, UUID groupId);
} 