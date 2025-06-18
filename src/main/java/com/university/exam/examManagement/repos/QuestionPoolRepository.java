package com.university.exam.examManagement.repos;

import com.university.exam.examManagement.entities.QuestionPool;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface QuestionPoolRepository extends JpaRepository<QuestionPool, UUID> {
} 