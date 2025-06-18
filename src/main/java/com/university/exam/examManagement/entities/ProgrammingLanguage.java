package com.university.exam.examManagement.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "programming_language")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProgrammingLanguage {
    @Id
    private UUID id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 50)
    private String codeName;

    @Column(length = 50)
    private String version;

    @Column(columnDefinition = "TEXT")
    private String compilerCommand;

    @Column(columnDefinition = "TEXT")
    private String runCommand;

    @Column(length = 10)
    private String fileExtension;

    @Column(nullable = false)
    private boolean enabled = true;
} 