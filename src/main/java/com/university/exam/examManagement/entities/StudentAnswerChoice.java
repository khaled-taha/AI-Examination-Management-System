package com.university.exam.examManagement.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "student_answer_choice")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentAnswerChoice {
    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID studentExamAttemptId;

    @Column(nullable = false)
    private UUID examQuestionId;

    private UUID selectedChoiceId;
} 