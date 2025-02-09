package com.university.exam.userManagement.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "ClassStudy")
public class ClassStudy {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "class_id")
    private UUID classId;

    @Column(nullable = false)
    private int year;

    @Column(name = "total_grade", nullable = false, precision = 5, scale = 2)
    private BigDecimal totalGrade;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
