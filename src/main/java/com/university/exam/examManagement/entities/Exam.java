package com.university.exam.examManagement.entities;

import com.university.exam.academicManagement.entities.AcademicTerm;
import com.university.exam.academicManagement.entities.AcademicYearCourse;
import com.university.exam.academicManagement.entities.AcademicYearGroup;
import com.university.exam.userManagement.entities.Admin;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "exam")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Exam {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false, length = 255)
    private String title;

    private String description;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime endDate;

    @Column(length = 50)
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id")
    private Admin creator;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "term_id")
    private AcademicTerm term;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "academic_year_group_id")
    private AcademicYearGroup academicYearGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "academic_year_course_id")
    private AcademicYearCourse academicYearCourse;

    @Column(nullable = false)
    private double successPercentage;

    @Column(nullable = false)
    private int allowedAttemptTimes;

    @Column(name = "questionsPerPage")
    private byte questionsPerPage;

    @Column(name = "showResult")
    private boolean showResult;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
} 