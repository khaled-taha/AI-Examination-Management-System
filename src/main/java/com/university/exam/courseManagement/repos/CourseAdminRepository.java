package com.university.exam.courseManagement.repos;

import com.university.exam.courseManagement.entities.CourseAdmin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CourseAdminRepository extends JpaRepository<CourseAdmin, UUID> {
    List<CourseAdmin> findByCourseCode(String courseCode);
    List<CourseAdmin> findByAdminAdminId(UUID adminId);
    void deleteByCourseCodeAndAdminAdminId(String courseCode, UUID adminId);
    void deleteByCourseCode(String courseCode);
}