package com.university.exam.examManagement.dtos.response;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class StudentCodingQuestionViewDTO extends StudentQuestionViewDTO {
    private UUID programmingLanguageId;
    private List<StudentCodingTestCaseViewDTO> testCases;
} 