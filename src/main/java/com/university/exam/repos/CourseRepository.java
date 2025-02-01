package com.university.exam.repos;

import com.university.exam.entities.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CourseRepository extends JpaRepository<Course, String> {
    List<Course> findByGroupId(UUID groupId);
}