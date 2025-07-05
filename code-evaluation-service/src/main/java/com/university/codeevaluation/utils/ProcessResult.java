package com.university.codeevaluation.utils;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProcessResult {
    private final String output;
    private final long memoryUsedKb;
    private final int exitCode;
}
