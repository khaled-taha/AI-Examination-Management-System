package com.university.exam.userManagement.repos;

import com.university.exam.userManagement.entities.ClassStudy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ClassStudyRepository extends JpaRepository<ClassStudy, UUID> {
}