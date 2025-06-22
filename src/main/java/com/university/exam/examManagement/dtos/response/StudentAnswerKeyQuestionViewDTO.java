package com.university.exam.examManagement.dtos.response;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class StudentAnswerKeyQuestionViewDTO extends StudentQuestionViewDTO {
    // For student view, we don't expose answer keys
    // Students just see the question text and need to provide their answers
} 