package com.university.exam.examManagement.dtos.response;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ExamQuestionChoiceResponseDTO extends ExamQuestionResponseDTO {
    private ExamQuestionChoicesResponseDTO choices;
} 