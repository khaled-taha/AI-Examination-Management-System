package com.university.exam.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "super_resource")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SuperResource {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Lob
    @Column(nullable = false, columnDefinition = "bytea")
    private byte[] data;

    @OneToOne
    @JoinColumn(name = "resource_id", nullable = false)
    private Resource resource;
}