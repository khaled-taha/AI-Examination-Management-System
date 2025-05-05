package com.university.exam.academicManagement.entities;

import com.university.exam.userManagement.entities.Student;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "student_enrollments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentEnrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne
    @JoinColumn(name = "academic_year_group_id", nullable = false)
    private AcademicYearGroup academicYearGroup;

    @ManyToOne
    @JoinColumn(name = "term_id", nullable = false)
    private AcademicTerm term;

    @Column(name = "enrollment_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private EnrollmentStatus enrollmentStatus;

    @CreationTimestamp
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime updatedAt;

    public enum EnrollmentStatus {
        ACTIVE, GRADUATED, WITHDRAWN, FAILED, REPEATING
    }
} 