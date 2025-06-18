package com.university.exam.examManagement.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "exam_section")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExamSection {
    @Id
    private UUID id;

    private UUID examId;

    @Column(length = 255)
    private String title;

    private Integer position;
} 