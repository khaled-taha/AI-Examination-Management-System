package com.university.codeevaluation.enums;

import lombok.Getter;

@Getter
public enum ProgrammingLanguage {
    JAVA("java", "javac", "java", ".java", "17"),
    PYTHON("python", "python", "python", ".py", "3.11"),
    C("c", "gcc", "", ".c", "11"),
    CPP("c++", "g++", "", ".cpp", "17"),
    SQL("sql", "mysql", "mysql", ".sql", "8.0");

    private final String name;
    private final String compilerCommand;
    private final String runCommand;
    private final String fileExtension;
    private final String version;

    ProgrammingLanguage(String name, String compilerCommand, String runCommand, String fileExtension, String version) {
        this.name = name;
        this.compilerCommand = compilerCommand;
        this.runCommand = runCommand;
        this.fileExtension = fileExtension;
        this.version = version;
    }

    public static ProgrammingLanguage fromName(String name) {
        for (ProgrammingLanguage lang : values()) {
            if (lang.getName().equalsIgnoreCase(name)) {
                return lang;
            }
        }
        throw new IllegalArgumentException("Unsupported programming language: " + name);
    }
} 