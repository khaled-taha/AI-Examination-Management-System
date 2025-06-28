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
        AcademicYearCourse course = findCourse(request.getAcademicYearCourseId());
        Admin admin = findAdmin(request.getUserId());
        validateAssignment(request.getAcademicYearCourseId(), admin.getAdminId());
        AcademicYearCourseAdmin savedAssignment = createAndSaveAssignment(course, admin);
        return AcademicYearCourseAdminResponseDTO.CourseAdminResponse.fromEntity(savedAssignment);
    }

    private AcademicYearCourse findCourse(UUID courseId) {
        return academicYearCourseRepo.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Academic year course [" + courseId + "] not found"));
    }

    private Admin findAdmin(UUID adminId) {
        return adminRepository.findByUser_UserId(adminId)
                .orElseThrow(() -> new EntityNotFoundException("Admin [" + adminId + "] not found"));
    }

    private void validateAssignment(UUID courseId, UUID adminId) {
        if (academicYearCourseAdminRepository.existsByAcademicYearCourseIdAndAdminId(courseId, adminId)) {
            throw new IllegalStateException("Admin is already assigned to this course");
        }
    }

    private AcademicYearCourseAdmin createAndSaveAssignment(AcademicYearCourse course, Admin admin) {
        AcademicYearCourseAdmin assignment = new AcademicYearCourseAdmin();
        assignment.setAcademicYearCourse(course);
        assignment.setAdmin(admin);
        return academicYearCourseAdminRepository.saveAndFlush(assignment);
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