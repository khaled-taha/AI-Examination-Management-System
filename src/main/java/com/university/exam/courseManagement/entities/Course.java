package com.university.exam.courseManagement.entities;

import com.university.exam.resourceManagement.entities.ResourceDirectory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "course")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course {
    @Id
    @Column(name = "code", nullable = false)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(name = "avatar_id")
    private UUID avatarId;

    @Column(nullable = false)
    private boolean active = true;

    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "dir_doc_id", nullable = false)
    private ResourceDirectory baseDirectory;
}
