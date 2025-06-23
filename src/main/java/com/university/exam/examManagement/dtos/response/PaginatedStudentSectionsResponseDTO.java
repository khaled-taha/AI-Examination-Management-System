package com.university.exam.examManagement.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaginatedStudentSectionsResponseDTO {
    private List<StudentSectionViewDTO> sections;
    private int currentPage;
    private int totalPages;
    private int totalQuestions;
    private int questionsPerPage;
    private boolean hasNextPage;
    private boolean hasPreviousPage;
} 