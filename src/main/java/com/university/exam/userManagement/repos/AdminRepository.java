package com.university.exam.userManagement.repos;

import com.university.exam.userManagement.entities.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AdminRepository extends JpaRepository<Admin, UUID> {
    Optional<Admin> findByUser_UserId(UUID userId);
    List<Admin> findBySpecialization_SpecializationId(UUID specializationId);
    List<Admin> findByUser_UserIdIn(List<UUID> userIds);
}
