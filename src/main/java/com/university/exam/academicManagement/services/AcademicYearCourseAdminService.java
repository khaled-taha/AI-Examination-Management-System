package com.university.exam.academicManagement.services;

import com.university.exam.academicManagement.dtos.requestDTO.AcademicYearCourseAdminRequestDTO;
import com.university.exam.academicManagement.dtos.responseDTO.AcademicYearCourseAdminResponseDTO;
import com.university.exam.academicManagement.entities.AcademicYearCourse;
import com.university.exam.academicManagement.entities.AcademicYearCourseAdmin;
import com.university.exam.academicManagement.repos.AcademicYearCourseAdminRepository;
import com.university.exam.academicManagement.repos.AcademicYearCourseRepository;
import com.university.exam.userManagement.entities.Admin;
import com.university.exam.userManagement.repos.AdminRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AcademicYearCourseAdminService {
    private final AcademicYearCourseAdminRepository academicYearCourseAdminRepository;
    private final AcademicYearCourseRepository academicYearCourseRepo;
    private final AdminRepository adminRepository;

    @Transactional
    public AcademicYearCourseAdminResponseDTO.CourseAdminResponse assignAdminToCourse(AcademicYearCourseAdminRequestDTO.AssignAdminRequest request) {
        // Check if course exists
        AcademicYearCourse course = academicYearCourseRepo.findById(request.getAcademicYearCourseId())
                .orElseThrow(() -> new EntityNotFoundException("Academic year course ["+ request.getAcademicYearCourseId() +"] not found"));

        // Check if admin exists
        Admin admin = adminRepository.findByUser_UserId(request.getAdminId())
                .orElseThrow(() -> new EntityNotFoundException("Admin ["+ request.getAdminId() +"] not found"));

        // Check if assignment already exists
        if (academicYearCourseAdminRepository.existsByAcademicYearCourseIdAndAdmin_AdminId(request.getAcademicYearCourseId(), request.getAdminId())) {
            throw new IllegalStateException("Admin is already assigned to this course");
        }

        // Create new assignment
        AcademicYearCourseAdmin assignment = new AcademicYearCourseAdmin();
        assignment.setAcademicYearCourse(course);
        assignment.setAdmin(admin);

        AcademicYearCourseAdmin savedAssignment = academicYearCourseAdminRepository.saveAndFlush(assignment);
        return AcademicYearCourseAdminResponseDTO.CourseAdminResponse.fromEntity(savedAssignment);
    }

    @Transactional
    public void removeAdminFromCourse(AcademicYearCourseAdminRequestDTO.RemoveAdminRequest request) {
        academicYearCourseAdminRepository.findByAcademicYearCourseId(request.getAcademicYearCourseId())
                .stream()
                .filter(assignment -> assignment.getAdmin().getAdminId().equals(request.getAdminId()))
                .findFirst()
                .ifPresent(academicYearCourseAdminRepository::delete);
    }

    @Transactional(readOnly = true)
    public List<AcademicYearCourseAdminResponseDTO.CourseAdminResponse> getCourseAdmins(UUID academicYearCourseId) {
        return academicYearCourseAdminRepository.findByAcademicYearCourseId(academicYearCourseId)
                .stream()
                .map(AcademicYearCourseAdminResponseDTO.CourseAdminResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AcademicYearCourseAdminResponseDTO.CourseAdminResponse> getAdminCourses(UUID adminId) {
        return academicYearCourseAdminRepository.findByAdmin_AdminId(adminId)
                .stream()
                .map(AcademicYearCourseAdminResponseDTO.CourseAdminResponse::fromEntity)
                .collect(Collectors.toList());
    }
}