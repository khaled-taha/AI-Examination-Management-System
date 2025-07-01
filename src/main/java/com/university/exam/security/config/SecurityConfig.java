package com.university.exam.security.config;

import com.university.exam.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers("/actuator/**").permitAll()
                        
                        // SuperAdmin endpoints - full access
                        .requestMatchers("/api/v1/**").hasAnyRole("SUPERADMIN", "ADMIN", "VIEWER", "STUDENT")
                        
                        // Admin endpoints - most access except super admin specific
                        .requestMatchers(
                                "/api/v1/admins/**",
                                "/api/v1/academic-years/**",
                                "/api/v1/courses/**",
                                "/api/v1/groups/**",
                                "/api/v1/exams/**",
                                "/api/v1/resources/**"
                        ).hasAnyRole("SUPERADMIN", "ADMIN")
                        
                        // Viewer endpoints - read-only access
                        .requestMatchers(
                                "GET:/api/v1/exams/**",
                                "GET:/api/v1/students/**",
                                "GET:/api/v1/admins/**",
                                "GET:/api/v1/academic-years/**",
                                "GET:/api/v1/courses/**",
                                "GET:/api/v1/groups/**",
                                "GET:/api/v1/resources/**"
                        ).hasAnyRole("SUPERADMIN", "ADMIN", "VIEWER")
                        
                        // Student endpoints - limited access
                        .requestMatchers(
                                "GET:/api/v1/exams/**",
                                "POST:/api/v1/student-exams/**",
                                "GET:/api/v1/student-exams/**",
                                "GET:/api/v1/exam-results/**"
                        ).hasAnyRole("SUPERADMIN", "ADMIN", "VIEWER", "STUDENT")
                        
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("*"));
        config.setAllowedMethods(List.of("*"));
        config.setAllowCredentials(false);
        config.setAllowedHeaders(List.of("*"));
        config.setMaxAge(2500L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
} 