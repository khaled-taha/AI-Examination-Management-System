package com.university.exam.examManagement.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentSectionViewDTO {
    private UUID id;
    private String title;
    private Integer position;
    private List<StudentQuestionViewDTO> questions;
} 