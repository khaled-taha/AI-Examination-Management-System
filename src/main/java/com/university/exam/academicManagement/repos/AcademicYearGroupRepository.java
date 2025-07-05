package com.university.exam.academicManagement.repos;

import com.university.exam.academicManagement.entities.AcademicYear;
import com.university.exam.academicManagement.entities.AcademicYearGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AcademicYearGroupRepository extends JpaRepository<AcademicYearGroup, UUID> {
    @Query("SELECT ag.academicYear FROM AcademicYearGroup ag WHERE ag.group.id = :groupId")
    List<AcademicYear> findByGroupId(@Param("groupId") UUID groupId);

    Optional<AcademicYearGroup> findByAcademicYearIdAndGroupId(UUID academicYearId, UUID groupId);

    Optional<AcademicYearGroup> findByAcademicYearId(UUID academicYearId);

    @Query("SELECT ayg FROM AcademicYearGroup ayg " +
            "JOIN AcademicTerm t ON t.academicYear = ayg.academicYear " +
            "WHERE ayg.group.id = :groupId " +
            "AND YEAR(ayg.academicYear.startDate) = YEAR(CURRENT_DATE) " +
            "AND t.termOrder = 1 AND t.status = 'ACTIVE' " +
            "ORDER BY ayg.academicYear.startDate DESC")
    Optional<AcademicYearGroup> findLatestActiveAcademicYearByGroupId(@Param("groupId") UUID groupId);

} 