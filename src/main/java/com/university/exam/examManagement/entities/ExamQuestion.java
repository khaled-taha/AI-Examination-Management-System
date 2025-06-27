package com.university.exam.examManagement.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "exam_question")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExamQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id")
    private Exam exam;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id")
    private ExamSection section;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_pool_id")
    private QuestionPool questionPool;

    @Column(columnDefinition = "TEXT")
    private String questionText;

    @Column(length = 50)
    private String questionType;

    @Column(columnDefinition = "TEXT")
    private String explanation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "programming_language_id")
    private ProgrammingLanguage programmingLanguage;

    private Integer timeLimit;
    private Integer memoryLimit;

    @Column(nullable = false)
    private double mark;

    private Integer position;
    private boolean active; // for soft-delete and grade.

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
} 