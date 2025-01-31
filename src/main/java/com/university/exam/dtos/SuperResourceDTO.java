package com.university.exam.dtos;

import lombok.Data;

import java.util.UUID;

@Data
public class SuperResourceDTO {
    private UUID id;
    private byte[] data;
    private UUID resourceId;
}
