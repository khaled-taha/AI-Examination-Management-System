package com.university.exam.examManagement.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "exam_question")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExamQuestion {
    @Id
    private UUID id;

    private UUID examId;
    private UUID sectionId;
    private UUID questionPoolId;

    @Column(columnDefinition = "TEXT")
    private String questionText;

    @Column(length = 50)
    private String questionType;

    @Column(columnDefinition = "TEXT")
    private String explanation;

    private UUID programmingLanguageId;
    private Integer timeLimit;
    private Integer memoryLimit;

    @Column(nullable = false)
    private double mark;

    private Integer position;
} 