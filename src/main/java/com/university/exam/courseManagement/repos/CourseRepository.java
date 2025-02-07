package com.university.exam.courseManagement.repos;

import com.university.exam.courseManagement.entities.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CourseRepository extends JpaRepository<Course, String> {
    List<Course> findByGroupId(UUID groupId);

    Optional<Course> findByCode(String code);
}