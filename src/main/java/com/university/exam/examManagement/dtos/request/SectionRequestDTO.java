package com.university.exam.examManagement.dtos.request;

import lombok.Data;
import jakarta.validation.constraints.*;
import java.util.UUID;

@Data
public class SectionRequestDTO {

    private UUID id;

    @NotNull(message = "Exam ID is required")
    private UUID examId;

    @NotBlank(message = "Section title must not be blank")
    private String title;

    @Min(value = 1, message = "Section position must be at least 1")
    private Integer position;
} 