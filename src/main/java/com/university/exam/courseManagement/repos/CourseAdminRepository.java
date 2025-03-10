package com.university.exam.courseManagement.repos;

import com.university.exam.courseManagement.entities.CourseAdmin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CourseAdminRepository extends JpaRepository<CourseAdmin, UUID> {
    List<CourseAdmin> findByCourseCode(String courseCode);
    void deleteByCourseCode(String courseCode);

    @Modifying(clearAutomatically = true)
    @Query(value = """
        DELETE FROM course_admin ca
        WHERE ca.admin_id = (SELECT admin_id FROM admin WHERE user_id = :userId)
        AND ca.course_code = :courseCode;
        """, nativeQuery = true)
    void deleteByCourseCodeAndAdminId(@Param("courseCode") String courseCode, @Param("userId") UUID adminId);
}