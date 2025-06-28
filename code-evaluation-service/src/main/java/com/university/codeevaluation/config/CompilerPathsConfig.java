package com.university.codeevaluation.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "code-evaluation.compilers")
public class CompilerPathsConfig {
    
    private Java java = new Java();
    private Python python = new Python();
    private C c = new C();
    private Cpp cpp = new Cpp();
    private Sql sql = new Sql();
    
    @Data
    public static class Java {
        private List<String> windowsPaths = List.of(
            "C:\\Program Files\\Java\\jdk-17\\bin",
            "C:\\Program Files\\Java\\jdk-11\\bin",
            "C:\\Program Files\\Java\\jre-17\\bin",
            "C:\\Program Files\\Eclipse Adoptium\\jdk-17.0.9.9-hotspot\\bin",
            "C:\\Program Files\\Eclipse Adoptium\\jdk-11.0.21.9-hotspot\\bin",
            "C:\\Program Files\\OpenJDK\\jdk-17\\bin",
            "C:\\Program Files\\OpenJDK\\jdk-11\\bin"
        );
        private List<String> linuxPaths = List.of(
            "/usr/bin",
            "/usr/local/bin",
            "/opt/java/bin"
        );
        private List<String> commands = List.of("java -version", "javac -version");
    }
    
    @Data
    public static class Python {
        private List<String> windowsPaths = List.of(
            "C:\\Python311",
            "C:\\Python310",
            "C:\\Python39",
            "C:\\Users\\%USERNAME%\\AppData\\Local\\Programs\\Python\\Python311",
            "C:\\Users\\%USERNAME%\\AppData\\Local\\Programs\\Python\\Python310"
        );
        private List<String> linuxPaths = List.of(
            "/usr/bin",
            "/usr/local/bin",
            "/opt/python/bin"
        );
        private List<String> commands = List.of("python --version", "python3 --version");
    }
    
    @Data
    public static class C {
        private List<String> windowsPaths = List.of(
            "C:\\MinGW\\bin",
            "C:\\mingw64\\bin",
            "C:\\Program Files\\mingw-w64\\x86_64-8.1.0-posix-seh-rt_v6-rev0\\mingw64\\bin"
        );
        private List<String> linuxPaths = List.of(
            "/usr/bin",
            "/usr/local/bin"
        );
        private List<String> commands = List.of("gcc --version");
    }
    
    @Data
    public static class Cpp {
        private List<String> windowsPaths = List.of(
            "C:\\MinGW\\bin",
            "C:\\mingw64\\bin",
            "C:\\Program Files\\mingw-w64\\x86_64-8.1.0-posix-seh-rt_v6-rev0\\mingw64\\bin"
        );
        private List<String> linuxPaths = List.of(
            "/usr/bin",
            "/usr/local/bin"
        );
        private List<String> commands = List.of("g++ --version");
    }
    
    @Data
    public static class Sql {
        private List<String> windowsPaths = List.of(
            "C:\\Program Files\\MySQL\\MySQL Server 8.0\\bin",
            "C:\\Program Files\\MySQL\\MySQL Server 5.7\\bin",
            "C:\\xampp\\mysql\\bin"
        );
        private List<String> linuxPaths = List.of(
            "/usr/bin",
            "/usr/local/bin",
            "/opt/mysql/bin"
        );
        private List<String> commands = List.of("mysql --version");
    }
} 