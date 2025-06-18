package com.university.exam.examManagement.repos;

import com.university.exam.examManagement.entities.ProgrammingLanguage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ProgrammingLanguageRepository extends JpaRepository<ProgrammingLanguage, UUID> {
} 