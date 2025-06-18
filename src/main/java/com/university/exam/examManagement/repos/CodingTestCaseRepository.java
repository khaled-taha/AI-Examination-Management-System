package com.university.exam.examManagement.repos;

import com.university.exam.examManagement.entities.CodingTestCase;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface CodingTestCaseRepository extends JpaRepository<CodingTestCase, UUID> {
} 