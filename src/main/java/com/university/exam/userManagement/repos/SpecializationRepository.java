package com.university.exam.userManagement.repos;

import com.university.exam.userManagement.entities.Specialization;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SpecializationRepository extends JpaRepository<Specialization, UUID> {
}
