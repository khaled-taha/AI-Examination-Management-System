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

    @Query(value = "SELECT ayg.* FROM academic_year_groups ayg " +
            "JOIN academic_terms t ON t.academic_year_id = ayg.academic_year_id " +
            "JOIN academic_years ay ON ay.id = ayg.academic_year_id " +
            "WHERE ayg.group_id = :groupId " +
            "AND EXTRACT(YEAR FROM ay.start_date) = EXTRACT(YEAR FROM CURRENT_DATE) " +
            "AND t.term_order = 1 AND t.status = 'ACTIVE' " +
            "ORDER BY ay.start_date DESC LIMIT 1", nativeQuery = true)
    Optional<AcademicYearGroup> findLatestActiveAcademicYearByGroupId(@Param("groupId") UUID groupId);
} 