package com.university.exam.academicManagement.services;

import com.university.exam.academicManagement.dtos.requestDTO.AcademicYearCourseRequestDTO;
import com.university.exam.academicManagement.dtos.requestDTO.AcademicYearRequestDTO;
import com.university.exam.academicManagement.dtos.responseDTO.AcademicYearCourseResponseDTO;
import com.university.exam.academicManagement.dtos.responseDTO.AcademicYearGroupResponseDTO;
import com.university.exam.academicManagement.dtos.responseDTO.AcademicYearResponseDTO;
import com.university.exam.academicManagement.entities.*;
import com.university.exam.academicManagement.repos.*;
import com.university.exam.courseManagement.entities.Group;
import com.university.exam.courseManagement.repos.GroupRepository;
import com.university.exam.exceptions.ResourceNotFoundException;
import com.university.exam.exceptions.ValidationException;
import com.university.exam.userManagement.dtos.responseDTO.StudentResponseDTO;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AcademicYearService {

    private final AcademicYearRepository academicYearRepository;
    private final GroupRepository groupRepository;
    private final AcademicTermRepository academicTermRepository;
    private final AcademicYearCourseRepository academicYearCourseRepository;
    private final AcademicYearGroupRepository academicYearGroupRepository;
    private final AcademicYearCourseAdminRepository academicYearCourseAdminRepository;
    private final StudentEnrollmentRepository studentEnrollmentRepository;

    @Transactional
    public AcademicYearResponseDTO.SaveYearResponse saveAcademicYearGroup(AcademicYearRequestDTO.SaveYearRequest request) {
        validateAcademicYear(request);
        AcademicYear academicYear = academicYearRepository.saveAndFlush(prepareAcademicYear(request));
        AcademicYearGroup academicYearGroup = this.academicYearGroupRepository.saveAndFlush(prepareAcademicYearGroup(academicYear, request.getGroupId()));
        return AcademicYearResponseDTO.SaveYearResponse.fromEntity(academicYear, academicYearGroup);
    }

    private void validateAcademicYear(AcademicYearRequestDTO.SaveYearRequest request) {
        if (request.getStartDate() == null || request.getEndDate() == null) {
            throw new ValidationException("Start date and end date are required");
        }

        if (!request.getStartDate().toString().matches("\\d{4}-\\d{2}-\\d{2}") ||
                !request.getEndDate().toString().matches("\\d{4}-\\d{2}-\\d{2}")) {
            throw new ValidationException("Date format must be YYYY-MM-DD");
        }

        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new ValidationException("Start date cannot be after end date");
        }

        if (academicYearRepository.existsByYear(request.getYear())) {
            throw new ValidationException("Academic year with year " + request.getYear() + " already exists");
        }

        if (!request.getYear().matches("\\d{4}-\\d{4}")) {
            throw new ValidationException("Year format must be YYYY-YYYY");
        }

        String[] years = request.getYear().split("-");
        int startYear = Integer.parseInt(years[0]);
        int endYear = Integer.parseInt(years[1]);
        if (endYear - startYear != 1) {
            throw new ValidationException("Academic year must span exactly one year");
        }
    }

    private AcademicYear prepareAcademicYear(AcademicYearRequestDTO.SaveYearRequest request) {
        AcademicYear academicYear = new AcademicYear();
        academicYear.setId(request.getId());
        academicYear.setYear(request.getYear());
        academicYear.setStartDate(request.getStartDate());
        academicYear.setEndDate(request.getEndDate());
        academicYear.setStatus("ACTIVE");
        return academicYear;
    }

    private AcademicYearGroup prepareAcademicYearGroup(AcademicYear academicYear, UUID groupId) {
        Optional<Group> group = this.groupRepository.findById(groupId);
        if(group.isEmpty()) throw new ValidationException("Group not found");

        Optional<AcademicYearGroup> academicGroup =
                this.academicYearGroupRepository.findByAcademicYearIdAndGroupId(academicYear.getId(), groupId);

        AcademicYearGroup academicYearGroup = academicGroup.orElseGet(AcademicYearGroup::new);
        academicYearGroup.setAcademicYear(academicYear);
        academicYearGroup.setGroup(group.get());
        return academicYearGroup;
    }

    @Transactional
    public AcademicYearResponseDTO.SaveAcademicYearTermsResponseDTO saveAcademicYearTerms(AcademicYearRequestDTO.SaveTermsRequest request) {
        AcademicYear academicYear = validateTerms(request);

        List<AcademicTerm> terms = request.getTerms().stream()
                .map(termDTO -> prepareAcademicTerm(termDTO, academicYear)).toList();

        academicTermRepository.saveAll(terms);
        return AcademicYearResponseDTO.SaveAcademicYearTermsResponseDTO.fromEntity(academicYear, terms);
    }

    private AcademicYear validateTerms(AcademicYearRequestDTO.SaveTermsRequest request) {

        AcademicYear academicYear = academicYearRepository.findById(request.getAcademicYearId())
                .orElseThrow(() -> new ValidationException("Academic year not found"));

        if (request.getTerms() == null || request.getTerms().isEmpty()) {
            throw new ValidationException("Terms cannot be empty");
        }

        request.getTerms().forEach(term -> {
            if (term.getStartDate() == null || term.getEndDate() == null) {
                throw new ValidationException("Start date and end date are required for term ["+ term.getName() +"]");
            }

            if (term.getStartDate().isBefore(academicYear.getStartDate()) ||
                    term.getEndDate().isAfter(academicYear.getEndDate())) {
                throw new ValidationException("Term ["+ term.getName() +"] dates must be within academic year date range");
            }

            if (term.getStartDate().isAfter(term.getEndDate())) {
                throw new ValidationException("Term ["+ term.getName() +"] start date cannot be after end date");
            }
        });

        return academicYear;
    }

    private AcademicTerm prepareAcademicTerm(AcademicYearRequestDTO.SaveTermsRequest.TermDTO termDTO, AcademicYear academicYear) {
        AcademicTerm term = new AcademicTerm();
        term.setId(termDTO.getTermId());
        term.setAcademicYear(academicYear);
        term.setName(termDTO.getName());
        term.setTermOrder(termDTO.getTermOrder());
        term.setStartDate(termDTO.getStartDate());
        term.setEndDate(termDTO.getEndDate());
        term.setStatus("ACTIVE");
        return term;
    }

    @Transactional
    public List<AcademicYearCourseResponseDTO> assignCoursesToTerm(AcademicYearCourseRequestDTO request) {
        List<AcademicYearCourse> courses = createAcademicYearCourses(request);
        return courses.stream()
                .map(AcademicYearCourseResponseDTO::fromEntity)
                .toList();
    }

    private List<AcademicYearCourse> createAcademicYearCourses(AcademicYearCourseRequestDTO request) {
        AcademicTerm term = academicTermRepository.findById(request.getTermId())
                .orElseThrow(() -> new ValidationException("Term not found."));

        academicYearCourseRepository.deleteByAcademicYearId(term.getAcademicYear().getId());
        academicYearCourseRepository.flush();

        List<AcademicYearCourse> courses = request.getCourseCodes().stream()
                .map(code -> prepareAcademicYearCourses(term.getAcademicYear(), code, term))
                .toList();
    
        return academicYearCourseRepository.saveAll(courses);
    }

    private AcademicYearCourse prepareAcademicYearCourses(AcademicYear academicYear, String courseCode, AcademicTerm term) {
        AcademicYearCourse course = new AcademicYearCourse();
        course.setAcademicYear(academicYear);
        course.setCourseCode(courseCode);
        course.setTerm(term);
        return course;
    }


    @Transactional(readOnly = true)
    public AcademicYearResponseDTO getAcademicYear(UUID academicYearId) {
        AcademicYear academicYear = getAcademicYearById(academicYearId);
        List<AcademicTerm> terms = getTermsByAcademicYearId(academicYearId);
        List<AcademicYearCourse> courses = getCoursesByAcademicYearId(academicYearId);
        Map<UUID, List<AcademicYearCourseAdmin>> adminsByCourseId = getAdminsByCourses(courses);

        return AcademicYearResponseDTO.fromEntity(academicYear, terms, courses, adminsByCourseId);
    }

    private AcademicYear getAcademicYearById(UUID academicYearId) {
        return academicYearRepository.findById(academicYearId)
                .orElseThrow(() -> new ResourceNotFoundException("Academic year not found"));
    }

    private List<AcademicTerm> getTermsByAcademicYearId(UUID academicYearId) {
        return academicTermRepository.findByAcademicYearId(academicYearId);
    }

    private List<AcademicYearCourse> getCoursesByAcademicYearId(UUID academicYearId) {
        return academicYearCourseRepository.findByAcademicYearId(academicYearId);
    }

    private Map<UUID, List<AcademicYearCourseAdmin>> getAdminsByCourses(List<AcademicYearCourse> courses) {
        if (courses.isEmpty()) {
            return Collections.emptyMap();
        }

        List<UUID> courseIds = courses.stream()
                .map(AcademicYearCourse::getId)
                .toList();

        List<AcademicYearCourseAdmin> courseAdmins = academicYearCourseAdminRepository
                .findByAcademicYearCourseIdIn(courseIds);

        return courseAdmins.stream()
                .collect(Collectors.groupingBy(admin -> admin.getAcademicYearCourse().getId()));
    }

    @Transactional(readOnly = true)
    public List<AcademicYearCourseResponseDTO> getAcademicYearCourses(UUID academicYearId) {
        // Verify academic year exists
        if (!academicYearRepository.existsById(academicYearId)) {
            throw new EntityNotFoundException("Academic year [" + academicYearId + "] not found");
        }
        return academicYearCourseRepository.findByAcademicYearId(academicYearId)
                .stream()
                .map(AcademicYearCourseResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AcademicYearResponseDTO.TermResponse> getAcademicYearTerms(UUID academicYearId) {
        // Verify academic year exists
        if (!academicYearRepository.existsById(academicYearId)) {
            throw new ResourceNotFoundException("Academic year not found");
        }

        // Get terms for this academic year
        List<AcademicTerm> terms = academicTermRepository.findByAcademicYearId(academicYearId);
        
        // Map to DTO
        return terms.stream()
            .map(AcademicYearResponseDTO.TermResponse::fromEntity)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AcademicYearResponseDTO> getAcademicYearsOfGroup(UUID groupId) {
        List<AcademicYear> academicYears = academicYearGroupRepository.findByGroupId(groupId);
        if (academicYears.isEmpty()) return Collections.emptyList();

        // Map academic years to DTOs using the grouped terms
        return academicYears.stream()
                .map(academicYear -> AcademicYearResponseDTO.fromEntity(academicYear, Collections.emptyList()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UUID getAcademicYearGroup(UUID academicYearId) {
        Optional<AcademicYearGroup> academicYearGroup = academicYearGroupRepository.findByAcademicYearId(academicYearId);
        return academicYearGroup.map(AcademicYearGroup::getId).orElse(null);
    }


    public List<StudentResponseDTO> getStudentsByAcademicYear(UUID academicYearId) {
        List<StudentEnrollment> enrollments = studentEnrollmentRepository
                .findByAcademicYearGroup_AcademicYear_IdAndEnrollmentStatus(
                        academicYearId, StudentEnrollment.EnrollmentStatus.ACTIVE);

        return enrollments.stream()
                .map(enrollment -> StudentResponseDTO.convertToStudentResponseDTO(enrollment.getStudent(), enrollment.getAcademicYearGroup()))
                .collect(Collectors.toList());
    }

}