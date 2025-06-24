package com.university.exam.examManagement.dtos.response;

import lombok.Data;
import java.util.UUID;
import java.util.List;

@Data
public class SectionResponseDTO {
    private UUID id;
    private UUID examId;
    private String title;
    private Integer position;
    private List<ExamQuestionResponseDTO> questions;
} 