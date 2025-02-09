package com.university.exam.userManagement.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Table(name = "Specialization ")
@Data
public class Specialization {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "specialization_id")
    private UUID specializationId;

    @Column(name = "specialization_name", nullable = false)
    private String specializationName;
}
