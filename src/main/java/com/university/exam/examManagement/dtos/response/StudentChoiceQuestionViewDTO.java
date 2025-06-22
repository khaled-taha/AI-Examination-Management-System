package com.university.exam.examManagement.dtos.response;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class StudentChoiceQuestionViewDTO extends StudentQuestionViewDTO {
    private List<StudentChoiceViewDTO> choices;
} 