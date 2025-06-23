package com.university.exam.examManagement.services;

import com.university.exam.academicManagement.entities.AcademicYearCourse;
import com.university.exam.academicManagement.repos.AcademicYearCourseRepository;
import com.university.exam.examManagement.dtos.request.*;
import com.university.exam.examManagement.dtos.response.*;
import com.university.exam.examManagement.entities.*;
import com.university.exam.examManagement.enums.ExamStatus;
import com.university.exam.examManagement.enums.QuestionType;
import com.university.exam.examManagement.repos.*;
import com.university.exam.academicManagement.entities.AcademicTerm;
import com.university.exam.academicManagement.entities.AcademicYearGroup;
import com.university.exam.userManagement.entities.Admin;
import com.university.exam.userManagement.entities.Student;
import com.university.exam.academicManagement.repos.AcademicTermRepository;
import com.university.exam.academicManagement.repos.AcademicYearGroupRepository;
import com.university.exam.userManagement.repos.AdminRepository;
import com.university.exam.userManagement.repos.StudentRepository;
import com.university.exam.utils.Utils;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service("DefaultExamServiceImpl")
@RequiredArgsConstructor
public class ExamServiceImpl implements ExamService {

    private final ExamRepository examRepository;
    private final ExamSectionRepository examSectionRepository;
    private final ExamQuestionRepository examQuestionRepository;
    private final ExamQuestionChoiceRepository examQuestionChoiceRepository;
    private final ExamQuestionAnswerKeyRepository examQuestionAnswerKeyRepository;
    private final CodingTestCaseRepository codingTestCaseRepository;
    private final StudentExamAttemptRepository studentExamAttemptRepository;
    private final StudentAnswerChoiceRepository studentAnswerChoiceRepository;
    private final StudentAnswerTextRepository studentAnswerTextRepository;
    private final StudentAnswerCodeRepository studentAnswerCodeRepository;
    private final StudentCodingTestResultRepository studentCodingTestResultRepository;
    private final ProgrammingLanguageRepository programmingLanguageRepository;
    private final QuestionPoolRepository questionPoolRepository;
    
    // External repositories for related entities
    private final AdminRepository adminRepository;
    private final StudentRepository studentRepository;
    private final AcademicTermRepository academicTermRepository;
    private final AcademicYearGroupRepository academicYearGroupRepository;
    private final AcademicYearCourseRepository academicYearCourseRepository;

    @Override
    @Transactional
    public ExamResponseDTO createExam(ExamRequestDTO request) {
        // Validate exam dates
        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Start date cannot be after end date");
        }

        // Validate related entities exist
        Admin creator = adminRepository.findById(request.getCreatorId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Admin not found with id: " + request.getCreatorId()));
        
        AcademicTerm term = academicTermRepository.findById(request.getTermId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Academic term not found with id: " + request.getTermId()));
        
        AcademicYearGroup academicYearGroup = academicYearGroupRepository.findById(request.getAcademicYearGroupId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Academic year group not found with id: " + request.getAcademicYearGroupId()));

        AcademicYearCourse academicYearCourse = academicYearCourseRepository.findById(request.getAcademicYearCourseId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Academic year Course not found with id: " + request.getAcademicYearCourseId()));

        if (LocalDateTime.now().isAfter(request.getEndDate())) {
            request.setStatus(ExamStatus.EXPIRED.name());
        }

        // Create new exam entity
        Exam exam = new Exam();
        exam.setId(UUID.randomUUID());
        exam.setTitle(request.getTitle());
        exam.setDescription(request.getDescription());
        exam.setStartDate(request.getStartDate());
        exam.setEndDate(request.getEndDate());
        exam.setStatus(request.getStatus() != null ? request.getStatus() : "DRAFT");
        exam.setCreator(creator);
        exam.setAcademicYearCourse(academicYearCourse);
        exam.setTerm(term);
        exam.setAcademicYearGroup(academicYearGroup);
        exam.setSuccessPercentage(request.getSuccessPercentage());
        exam.setAllowedAttemptTimes(request.getAllowedAttemptTimes());
        exam.setQuestionsPerPage(request.getQuestionsPerPage());
        exam.setShowResult(request.isShowResult());
        exam.setCreatedAt(LocalDateTime.now());
        exam.setUpdatedAt(LocalDateTime.now());

        Exam savedExam = examRepository.save(exam);
        return convertToExamResponseDTO(savedExam);
    }

    @Override
    @Transactional
    public ExamResponseDTO getExam(UUID id) {
        Exam exam = examRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Exam not found with id: " + id));
        return convertToExamResponseDTO(exam);
    }

    @Override
    @Transactional
    public ExamResponseDTO updateExam(UUID id, ExamRequestDTO request) {
        Exam exam = examRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Exam not found with id: " + id));

        // Validate exam dates
        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Start date cannot be after end date");
        }

        // Validate related entities exist
        Admin creator = adminRepository.findById(request.getCreatorId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Admin not found with id: " + request.getCreatorId()));
        
        AcademicTerm term = academicTermRepository.findById(request.getTermId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Academic term not found with id: " + request.getTermId()));
        
        AcademicYearGroup academicYearGroup = academicYearGroupRepository.findById(request.getAcademicYearGroupId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Academic year group not found with id: " + request.getAcademicYearGroupId()));

        AcademicYearCourse academicYearCourse = academicYearCourseRepository.findById(request.getAcademicYearCourseId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Academic year Course not found with id: " + request.getAcademicYearCourseId()));

        if (LocalDateTime.now().isAfter(request.getEndDate())) {
            request.setStatus(ExamStatus.EXPIRED.name());
        }

        // Update exam fields
        exam.setTitle(request.getTitle());
        exam.setDescription(request.getDescription());
        exam.setStartDate(request.getStartDate());
        exam.setEndDate(request.getEndDate());
        exam.setStatus(request.getStatus());
        exam.setCreator(creator);
        exam.setAcademicYearCourse(academicYearCourse);
        exam.setTerm(term);
        exam.setAcademicYearGroup(academicYearGroup);
        exam.setSuccessPercentage(request.getSuccessPercentage());
        exam.setAllowedAttemptTimes(request.getAllowedAttemptTimes());
        exam.setQuestionsPerPage(request.getQuestionsPerPage());
        exam.setShowResult(request.isShowResult());
        exam.setUpdatedAt(LocalDateTime.now());

        Exam updatedExam = examRepository.save(exam);
        return convertToExamResponseDTO(updatedExam);
    }

    @Override
    public void deleteExam(UUID id) {
        Exam exam = examRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Exam not found with id: " + id));
        
        // Check if exam has any attempts
        List<StudentExamAttempt> attempts = studentExamAttemptRepository.findByExamId(id);
        if (!attempts.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot delete exam with existing attempts");
        }

        examRepository.delete(exam);
    }

    @Override
    @Transactional
    public List<ExamResponseDTO> getExams() {
        List<Exam> exams = examRepository.findAll();
        return exams.stream()
                .map(this::convertToExamResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<ExamResponseDTO> getExamsByAcademicYearCourseId(UUID academicYearCourseId) {
        // Use the new repository method with @Query
        List<Exam> exams = examRepository.findExamsByAcademicYearCourse(academicYearCourseId);
        
        return exams.stream()
                .map(this::convertToExamResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CanEnterExamResponseDTO canStudentEnterExam(UUID studentId, UUID examId) {
        // Validate that exam exists
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Exam not found with id: " + examId));

        // Check if exam is currently active
        if (!"ACTIVE".equals(exam.getStatus())) {
            return new CanEnterExamResponseDTO(false, false,"Exam is not currently active", "Exam status: " + exam.getStatus());
        }

        // Check if current time is within exam duration
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(exam.getStartDate()) || now.isAfter(exam.getEndDate())) {
            return new CanEnterExamResponseDTO(false, false, "Exam is not available at this time",
                "Available from " + exam.getStartDate() + " to " + exam.getEndDate());
        }

        // Check the latest attempt to see if it's completed (has endTime)
        StudentExamAttempt latestAttempt = studentExamAttemptRepository.findLatestAttemptByStudentAndExam(studentId, examId)
                .orElse(null);

        if (latestAttempt == null) {
            return new CanEnterExamResponseDTO(false, true, "Student should attempt exam", "No previous attempts found");
        }

        // If the latest attempt has no endTime (null), student can continue that attempt
        if (latestAttempt.getEndTime() == null) {
            return new CanEnterExamResponseDTO(true, false, "Student can continue previous attempt",
                    "Previous attempt (ID: " + latestAttempt.getId() + ") is still in progress");
        }

        if (latestAttempt.getAttemptNumber() >= exam.getAllowedAttemptTimes()) {
            return new CanEnterExamResponseDTO(false, false, "Student has exceeded allowed attempts",
                "Allowed: " + exam.getAllowedAttemptTimes() + ", Current: " + latestAttempt.getAttemptNumber());
        }

        // If the latest attempt has endTime, student needs to start a new attempt
        return new CanEnterExamResponseDTO(false, true, "Student needs to start a new attempt",
            "Previous attempt completed at " + latestAttempt.getEndTime());
    }

    // Helper method to convert Exam entity to ExamResponseDTO
    private ExamResponseDTO convertToExamResponseDTO(Exam exam) {
        ExamResponseDTO response = new ExamResponseDTO();
        response.setId(exam.getId());
        response.setTitle(exam.getTitle());
        response.setStartDate(exam.getStartDate());
        response.setEndDate(exam.getEndDate());
        response.setStatus(exam.getStatus());
        response.setCreatorId(exam.getCreator().getAdminId());
        response.setCourseCode(exam.getAcademicYearCourse().getCourseCode());
        response.setTermId(exam.getTerm().getId());
        response.setAcademicYearGroupId(exam.getAcademicYearGroup().getId());
        response.setSuccessPercentage(exam.getSuccessPercentage());
        response.setAllowedAttemptTimes(exam.getAllowedAttemptTimes());
        exam.setQuestionsPerPage(exam.getQuestionsPerPage());
        exam.setShowResult(exam.isShowResult());
        response.setCreationTime(exam.getCreatedAt());
        return response;
    }

    @Override
    public SectionResponseDTO createSection(UUID examId, SectionRequestDTO request) {
        // Validate that exam exists
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Exam not found with id: " + examId));

        // Create new section entity
        ExamSection section = new ExamSection();
        section.setId(UUID.randomUUID());
        section.setExam(exam);
        section.setTitle(request.getTitle());
        section.setPosition(request.getPosition());
        section.setCreatedAt(LocalDateTime.now());
        section.setUpdatedAt(LocalDateTime.now());

        ExamSection savedSection = examSectionRepository.save(section);
        return convertToSectionResponseDTO(savedSection);
    }

    @Override
    @Transactional
    public List<SectionResponseDTO> getSections(UUID examId) {
        // Validate that exam exists
        examRepository.findById(examId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Exam not found with id: " + examId));

        List<ExamSection> sections = examSectionRepository.findByExamId(examId);
        return sections.stream()
                .map(this::convertSectionToResponseDTOWithQuestions)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PaginatedSectionsResponseDTO getSectionsPaginated(UUID examId, int page) {
        // Validate that exam exists
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Exam not found with id: " + examId));

        // Validate page number
        if (page < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Page number must be greater than 0");
        }

        int questionsPerPage = exam.getQuestionsPerPage();
        if (questionsPerPage <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Exam questionsPerPage must be greater than 0");
        }

        // Get all sections with their questions
        List<ExamSection> allSections = examSectionRepository.findByExamId(examId);
        
        // Collect all questions from all sections with their section info
        List<QuestionWithSection> allQuestions = new ArrayList<>();
        for (ExamSection section : allSections) {
            List<ExamQuestion> questions = examQuestionRepository.findBySection(section);
            for (ExamQuestion question : questions) {
                allQuestions.add(new QuestionWithSection(question, section));
            }
        }

        // Sort questions by section position, then by question position
        allQuestions.sort(Comparator.
                comparingInt((QuestionWithSection q) -> q.section.getPosition())
                .thenComparingInt(q -> q.question.getPosition()));

        int totalQuestions = allQuestions.size();
        int totalPages = (int) Math.ceil((double) totalQuestions / questionsPerPage);

        // Validate page number
        if (page > totalPages && totalPages > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Page " + page + " does not exist. Total pages: " + totalPages);
        }

        // Calculate start and end indices for the requested page
        int startIndex = (page - 1) * questionsPerPage;
        int endIndex = Math.min(startIndex + questionsPerPage, totalQuestions);

        // Get questions for the current page
        List<QuestionWithSection> pageQuestions = allQuestions.subList(startIndex, endIndex);

        // Group questions by section
        Map<ExamSection, List<ExamQuestion>> questionsBySection = pageQuestions.stream()
                .collect(Collectors.groupingBy(
                    qws -> qws.section,
                    Collectors.mapping(qws -> qws.question, Collectors.toList())
                ));

        // Create section DTOs with only the questions for this page
        List<SectionResponseDTO> sections = new ArrayList<>();
        for (ExamSection section : allSections) {
            List<ExamQuestion> sectionQuestions = questionsBySection.get(section);
            if (sectionQuestions != null && !sectionQuestions.isEmpty()) {
                SectionResponseDTO sectionDto = new SectionResponseDTO();
                sectionDto.setId(section.getId());
                sectionDto.setExamId(section.getExam().getId());
                sectionDto.setTitle(section.getTitle());
                sectionDto.setPosition(section.getPosition());
                
                // Convert questions to DTOs
                sectionDto.setQuestions(sectionQuestions.stream()
                        .map(this::convertQuestionToPolymorphicDTO)
                        .collect(Collectors.toList()));
                
                sections.add(sectionDto);
            }
        }

        // Create pagination response
        PaginatedSectionsResponseDTO response = new PaginatedSectionsResponseDTO();
        response.setSections(sections);
        response.setCurrentPage(page);
        response.setTotalPages(totalPages);
        response.setTotalQuestions(totalQuestions);
        response.setQuestionsPerPage(questionsPerPage);
        response.setHasNextPage(page < totalPages);
        response.setHasPreviousPage(page > 1);

        return response;
    }

    private SectionResponseDTO convertSectionToResponseDTOWithQuestions(ExamSection section) {
        SectionResponseDTO dto = new SectionResponseDTO();
        dto.setId(section.getId());
        dto.setExamId(section.getExam().getId());
        dto.setTitle(section.getTitle());
        dto.setPosition(section.getPosition());

        List<ExamQuestion> questions = examQuestionRepository.findBySection(section);
        dto.setQuestions(questions.stream()
                .map(this::convertQuestionToPolymorphicDTO)
                .collect(Collectors.toList()));
        return dto;
    }

    private QuestionResponseDTO convertQuestionToPolymorphicDTO(ExamQuestion question) {
        QuestionResponseDTO dto;

        switch (QuestionType.valueOf(question.getQuestionType())) {
            case TF:
            case MCQ:
            case MultiChoice:
                ChoiceQuestionResponseDTO choiceDto = new ChoiceQuestionResponseDTO();
                List<ExamQuestionChoice> choices = examQuestionChoiceRepository.findByExamQuestion(question);
                choiceDto.setChoices(choices.stream()
                        .map(this::convertToChoiceResponseDTO)
                        .collect(Collectors.toList()));
                dto = choiceDto;
                break;

            case Complete:
            case Matching:
                AnswerKeyQuestionResponseDTO answerDto = new AnswerKeyQuestionResponseDTO();
                List<ExamQuestionAnswerKey> answerKeys = examQuestionAnswerKeyRepository.findByExamQuestion(question);
                answerDto.setAnswerKeys(answerKeys.stream()
                        .map(this::convertToAnswerKeyResponseDTO)
                        .collect(Collectors.toList()));
                dto = answerDto;
                break;

            case Coding:
                CodingQuestionResponseDTO codingDto = new CodingQuestionResponseDTO();
                if (question.getProgrammingLanguage() != null) {
                    codingDto.setProgrammingLanguageId(question.getProgrammingLanguage().getId());
                }
                List<CodingTestCase> testCases = codingTestCaseRepository.findByExamQuestion(question);
                codingDto.setTestCases(testCases.stream()
                        .map(this::convertToCodingTestCaseResponseDTO)
                        .collect(Collectors.toList()));
                dto = codingDto;

                break;

            default:
                throw new IllegalStateException("Unsupported question type: " + question.getQuestionType());
        }
        
        // Populate common fields
        dto.setId(question.getId());
        if (question.getExam() != null) {
            dto.setExamId(question.getExam().getId());
        }
        if (question.getSection() != null) {
            dto.setSectionId(question.getSection().getId());
        }
        if (question.getQuestionPool() != null) {
            dto.setQuestionPoolId(question.getQuestionPool().getId());
        }
        dto.setQuestionText(question.getQuestionText());
        dto.setQuestionType(question.getQuestionType());
        dto.setExplanation(question.getExplanation());
        dto.setTimeLimit(question.getTimeLimit());
        dto.setMemoryLimit(question.getMemoryLimit());
        dto.setMark(question.getMark());
        dto.setPosition(question.getPosition());
        dto.setActive(question.isActive());

        return dto;
    }

    // Helper method to convert ExamSection entity to SectionResponseDTO
    private SectionResponseDTO convertToSectionResponseDTO(ExamSection section) {
        SectionResponseDTO response = new SectionResponseDTO();
        response.setId(section.getId());
        response.setExamId(section.getExam().getId());
        response.setTitle(section.getTitle());
        response.setPosition(section.getPosition());
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentSectionViewDTO> getExamForStudent(UUID examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Exam not found with id: " + examId));

        List<ExamSection> sections = examSectionRepository.findByExam(exam);

        return sections.stream()
                .map(this::convertSectionToStudentViewDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedStudentSectionsResponseDTO getExamForStudentPaginated(UUID examId, int page) {
        // Validate that exam exists
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Exam not found with id: " + examId));

        // Validate page number
        if (page < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Page number must be greater than 0");
        }

        int questionsPerPage = exam.getQuestionsPerPage();
        if (questionsPerPage <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Exam questionsPerPage must be greater than 0");
        }

        // Get all sections with their questions
        List<ExamSection> allSections = examSectionRepository.findByExam(exam);
        
        // Collect all questions from all sections with their section info
        List<QuestionWithSection> allQuestions = new ArrayList<>();
        for (ExamSection section : allSections) {
            List<ExamQuestion> questions = examQuestionRepository.findBySection(section);
            for (ExamQuestion question : questions) {
                allQuestions.add(new QuestionWithSection(question, section));
            }
        }

        // Sort questions by section position, then by question position
        allQuestions.sort((q1, q2) -> {
            int sectionCompare = Integer.compare(q1.section.getPosition(), q2.section.getPosition());
            if (sectionCompare != 0) {
                return sectionCompare;
            }
            return Integer.compare(q1.question.getPosition(), q2.question.getPosition());
        });

        int totalQuestions = allQuestions.size();
        int totalPages = (int) Math.ceil((double) totalQuestions / questionsPerPage);

        // Validate page number
        if (page > totalPages && totalPages > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Page " + page + " does not exist. Total pages: " + totalPages);
        }

        // Calculate start and end indices for the requested page
        int startIndex = (page - 1) * questionsPerPage;
        int endIndex = Math.min(startIndex + questionsPerPage, totalQuestions);

        // Get questions for the current page
        List<QuestionWithSection> pageQuestions = allQuestions.subList(startIndex, endIndex);

        // Group questions by section
        Map<ExamSection, List<ExamQuestion>> questionsBySection = pageQuestions.stream()
                .collect(Collectors.groupingBy(
                    qws -> qws.section,
                    Collectors.mapping(qws -> qws.question, Collectors.toList())
                ));

        // Create section DTOs with only the questions for this page
        List<StudentSectionViewDTO> sections = new ArrayList<>();
        for (ExamSection section : allSections) {
            List<ExamQuestion> sectionQuestions = questionsBySection.get(section);
            if (sectionQuestions != null && !sectionQuestions.isEmpty()) {
                StudentSectionViewDTO sectionDto = new StudentSectionViewDTO();
                sectionDto.setId(section.getId());
                sectionDto.setTitle(section.getTitle());
                sectionDto.setPosition(section.getPosition());
                
                // Convert questions to student view DTOs
                sectionDto.setQuestions(sectionQuestions.stream()
                        .map(this::convertQuestionToStudentViewDTO)
                        .collect(Collectors.toList()));
                
                sections.add(sectionDto);
            }
        }

        // Create pagination response
        PaginatedStudentSectionsResponseDTO response = new PaginatedStudentSectionsResponseDTO();
        response.setSections(sections);
        response.setCurrentPage(page);
        response.setTotalPages(totalPages);
        response.setTotalQuestions(totalQuestions);
        response.setQuestionsPerPage(questionsPerPage);
        response.setHasNextPage(page < totalPages);
        response.setHasPreviousPage(page > 1);

        return response;
    }

    private StudentSectionViewDTO convertSectionToStudentViewDTO(ExamSection section) {
        StudentSectionViewDTO sectionViewDTO = new StudentSectionViewDTO();
        sectionViewDTO.setId(section.getId());
        sectionViewDTO.setTitle(section.getTitle());
        sectionViewDTO.setPosition(section.getPosition());

        List<ExamQuestion> questions = examQuestionRepository.findBySection(section);
        sectionViewDTO.setQuestions(questions.stream()
                .map(this::convertQuestionToStudentViewDTO)
                .collect(Collectors.toList()));

        return sectionViewDTO;
    }

    private StudentQuestionViewDTO convertQuestionToStudentViewDTO(ExamQuestion question) {
        StudentQuestionViewDTO questionViewDTO = StudentQuestionViewDTO.createStudentQuestionView(question.getQuestionType());
        
        // Set common fields
        questionViewDTO.setId(question.getId());
        questionViewDTO.setQuestionText(question.getQuestionText());
        questionViewDTO.setQuestionType(question.getQuestionType());
        questionViewDTO.setExplanation(question.getExplanation());
        questionViewDTO.setMark(question.getMark());
        questionViewDTO.setPosition(question.getPosition());
        questionViewDTO.setTimeLimit(question.getTimeLimit());
        questionViewDTO.setMemoryLimit(question.getMemoryLimit());
        questionViewDTO.setActive(question.isActive());

        // Set type-specific fields
        switch (QuestionType.valueOf(question.getQuestionType())) {
            case TF:
            case MCQ:
            case MultiChoice:
                StudentChoiceQuestionViewDTO choiceDto = (StudentChoiceQuestionViewDTO) questionViewDTO;
                // Fetch choices without correct answers
                List<ExamQuestionChoice> choices = examQuestionChoiceRepository.findByExamQuestion(question);
                choiceDto.setChoices(choices.stream()
                        .map(choice -> new StudentChoiceViewDTO(choice.getId(), choice.getChoiceText()))
                        .collect(Collectors.toList()));
                break;

            case Complete:
            case Matching:
                // For answer key questions, students just see the question text
                // No additional data needed in student view
                break;

            case Coding:
                StudentCodingQuestionViewDTO codingDto = (StudentCodingQuestionViewDTO) questionViewDTO;
                if (question.getProgrammingLanguage() != null) {
                    codingDto.setProgrammingLanguageId(question.getProgrammingLanguage().getId());
                }
                // Fetch only sample test cases
                List<CodingTestCase> sampleTestCases = codingTestCaseRepository.findByExamQuestionAndIsSample(question, true);
                codingDto.setTestCases(sampleTestCases.stream()
                        .map(testCase -> new StudentCodingTestCaseViewDTO(testCase.getId(), testCase.getInput(), testCase.getExpectedOutput(), testCase.getMark()))
                        .collect(Collectors.toList()));
                break;

            default:
                throw new IllegalStateException("Unsupported question type: " + question.getQuestionType());
        }

        return questionViewDTO;
    }

    @Override
    @Transactional
    public QuestionResponseDTO addQuestion(UUID examId, QuestionRequestDTO request) {
        // Validate that exam exists
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Exam not found with id: " + examId));

        // Validate that section exists if provided
        ExamSection section = null;
        if (request.getSectionId() != null) {
            section = examSectionRepository.findById(request.getSectionId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Section not found with id: " + request.getSectionId()));
        }

        // Validate that question pool exists if provided
        QuestionPool questionPool = null;
        if (request.getQuestionPoolId() != null) {
            questionPool = questionPoolRepository.findById(request.getQuestionPoolId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Question pool not found with id: " + request.getQuestionPoolId()));
        }

        // Validate that programming language exists if provided
        ProgrammingLanguage programmingLanguage = null;
        if (request.getProgrammingLanguageId() != null) {
            programmingLanguage = programmingLanguageRepository.findById(request.getProgrammingLanguageId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Programming language not found with id: " + request.getProgrammingLanguageId()));
        }

        // Create new question entity
        ExamQuestion question = new ExamQuestion();
        question.setId(UUID.randomUUID());
        question.setExam(exam);
        question.setSection(section);
        question.setQuestionPool(questionPool);
        question.setQuestionText(request.getQuestionText());
        question.setQuestionType(request.getQuestionType());
        question.setExplanation(request.getExplanation());
        question.setProgrammingLanguage(programmingLanguage);
        question.setTimeLimit(request.getTimeLimit());
        question.setMemoryLimit(request.getMemoryLimit());
        question.setMark(request.getMark());
        question.setPosition(request.getPosition());
        question.setActive(request.isActive());
        question.setCreatedAt(LocalDateTime.now());
        question.setUpdatedAt(LocalDateTime.now());

        ExamQuestion savedQuestion = examQuestionRepository.save(question);
        return convertToQuestionResponseDTO(savedQuestion);
    }

    @Override
    @Transactional
    public QuestionResponseDTO updateQuestion(UUID questionId, QuestionRequestDTO request) {
        ExamQuestion question = examQuestionRepository.findById(questionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Question not found with id: " + questionId));

        // Validate that section exists if provided
        ExamSection examSection = null;
        if (request.getSectionId() != null) {
            examSection = examSectionRepository.findById(request.getSectionId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Section not found with id: " + request.getSectionId()));
        }

        // Validate that question pool exists if provided
        QuestionPool questionPool = null;
        if (request.getQuestionPoolId() != null) {
            questionPool = questionPoolRepository.findById(request.getQuestionPoolId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Question pool not found with id: " + request.getQuestionPoolId()));
        }

        // Validate that programming language exists if provided
        ProgrammingLanguage programmingLanguage = null;
        if (request.getProgrammingLanguageId() != null) {
            programmingLanguage = programmingLanguageRepository.findById(request.getProgrammingLanguageId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Programming language not found with id: " + request.getProgrammingLanguageId()));
        }

        // Update question fields
        question.setSection(examSection);
        question.setQuestionPool(questionPool);
        question.setQuestionText(request.getQuestionText());
        question.setQuestionType(request.getQuestionType());
        question.setExplanation(request.getExplanation());
        question.setProgrammingLanguage(programmingLanguage);
        question.setTimeLimit(request.getTimeLimit());
        question.setMemoryLimit(request.getMemoryLimit());
        question.setMark(request.getMark());
        question.setPosition(request.getPosition());
        question.setActive(request.isActive());
        question.setUpdatedAt(LocalDateTime.now());

        ExamQuestion updatedQuestion = examQuestionRepository.save(question);
        return convertToQuestionResponseDTO(updatedQuestion);
    }

    @Override
    @Transactional
    public void deleteQuestion(UUID questionId) {
        ExamQuestion question = examQuestionRepository.findById(questionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Question not found with id: " + questionId));

        boolean allowedDeletion = switch (QuestionType.valueOf(question.getQuestionType())) {
            case TF, MCQ, MultiChoice -> studentAnswerChoiceRepository.existsByQuestionId(questionId);
            case Complete, Matching -> studentAnswerCodeRepository.existsByQuestionId(questionId);
            case Coding -> studentAnswerTextRepository.existsByQuestionId(questionId);
        };

        if(allowedDeletion) examQuestionRepository.delete(question);
    }

    @Override
    @Transactional
    public ChoiceResponseDTO addChoice(UUID questionId, ChoiceRequestDTO request) {
        // Validate that question exists
        ExamQuestion question = examQuestionRepository.findById(questionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Question not found with id: " + questionId));

        // Create new choice entity
        ExamQuestionChoice choice = new ExamQuestionChoice();
        choice.setId(UUID.randomUUID());
        choice.setExamQuestion(question);
        choice.setChoiceText(request.getChoiceText());
        choice.setIsCorrect(request.getIsCorrect());
        choice.setMarkValue(request.getMarkValue());
        choice.setCreatedAt(LocalDateTime.now());
        choice.setUpdatedAt(LocalDateTime.now());

        ExamQuestionChoice savedChoice = examQuestionChoiceRepository.save(choice);
        return convertToChoiceResponseDTO(savedChoice);
    }

    @Override
    @Transactional
    public List<ChoiceResponseDTO> getChoices(UUID questionId) {
        // Validate that question exists
        examQuestionRepository.findById(questionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Question not found with id: " + questionId));

        List<ExamQuestionChoice> choices = examQuestionChoiceRepository.findByExamQuestionId(questionId);
        return choices.stream()
                .map(this::convertToChoiceResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AnswerKeyResponseDTO addAnswerKey(UUID questionId, AnswerKeyRequestDTO request) {
        // Validate that question exists
        ExamQuestion question = examQuestionRepository.findById(questionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Question not found with id: " + questionId));

        // Create new answer key entity
        ExamQuestionAnswerKey answerKey = new ExamQuestionAnswerKey();
        answerKey.setId(UUID.randomUUID());
        answerKey.setExamQuestion(question);
        answerKey.setAnswerText(request.getAnswerText());
        answerKey.setQuestionPart(request.getQuestionPart());
        answerKey.setCaseSensitive(request.isCaseSensitive());
        answerKey.setSortOrder(request.getSortOrder());
        answerKey.setCreatedAt(LocalDateTime.now());
        answerKey.setUpdatedAt(LocalDateTime.now());

        ExamQuestionAnswerKey savedAnswerKey = examQuestionAnswerKeyRepository.save(answerKey);
        return convertToAnswerKeyResponseDTO(savedAnswerKey);
    }

    @Override
    @Transactional
    public List<AnswerKeyResponseDTO> getAnswerKeys(UUID questionId) {
        // Validate that question exists
        examQuestionRepository.findById(questionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Question not found with id: " + questionId));

        List<ExamQuestionAnswerKey> answerKeys = examQuestionAnswerKeyRepository.findByExamQuestionId(questionId);
        return answerKeys.stream()
                .map(this::convertToAnswerKeyResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CodingTestCaseResponseDTO addTestCase(UUID questionId, CodingTestCaseRequestDTO request) {
        // Validate that question exists
       ExamQuestion examQuestion = examQuestionRepository.findById(questionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Question not found with id: " + questionId));

        // Create new test case entity
        CodingTestCase testCase = new CodingTestCase();
        testCase.setId(UUID.randomUUID());
        testCase.setExamQuestion(examQuestion);
        testCase.setInput(request.getInput());
        testCase.setExpectedOutput(request.getExpectedOutput());
        testCase.setMark(request.getMark());
        testCase.setSample(request.isSample());
        testCase.setCreatedAt(LocalDateTime.now());
        testCase.setUpdatedAt(LocalDateTime.now());

        CodingTestCase savedTestCase = codingTestCaseRepository.save(testCase);
        return convertToCodingTestCaseResponseDTO(savedTestCase);
    }

    @Override
    @Transactional
    public List<CodingTestCaseResponseDTO> getTestCases(UUID questionId) {
        // Validate that question exists
        examQuestionRepository.findById(questionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Question not found with id: " + questionId));

        List<CodingTestCase> testCases = codingTestCaseRepository.findByExamQuestionId(questionId);
        return testCases.stream()
                .map(this::convertToCodingTestCaseResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public StudentAttemptResponseDTO createStudentAttempt(StudentAttemptRequestDTO request) {
        // Validate that exam exists
        Exam exam = examRepository.findById(request.getExamId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Exam not found with id: " + request.getExamId()));

        // Validate that student exists
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found with id: " + request.getStudentId()));

        // Enhanced validation: Check exam status and time constraints
        LocalDateTime now = LocalDateTime.now();
        
        // Check if exam is currently active
        if (!"ACTIVE".equals(exam.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Exam is not currently active. Status: " + exam.getStatus());
        }
        
        // Check if current time is within exam duration
        if (now.isBefore(exam.getStartDate()) || now.isAfter(exam.getEndDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Exam is not available at this time. Available from " + exam.getStartDate() + " to " + exam.getEndDate());
        }

        // Check if student has exceeded allowed attempts
        List<StudentExamAttempt> existingAttempts = studentExamAttemptRepository.findByStudentIdAndExamId(request.getStudentId(), request.getExamId());
        if (existingAttempts.size() >= exam.getAllowedAttemptTimes()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Student has exceeded allowed attempts for this exam. Allowed: " + exam.getAllowedAttemptTimes() + ", Current: " + existingAttempts.size());
        }

        // Create new student attempt entity
        StudentExamAttempt attempt = new StudentExamAttempt();
        attempt.setId(UUID.randomUUID());
        attempt.setStudent(student);
        attempt.setExam(exam);
        attempt.setAttemptNumber(existingAttempts.size() + 1); // Auto-calculate attempt number
        attempt.setStartTime(now);
        attempt.setStatus("IN_PROGRESS");
        attempt.setCreatedAt(now);
        attempt.setUpdatedAt(now);

        StudentExamAttempt savedAttempt = studentExamAttemptRepository.save(attempt);
        return convertToStudentAttemptResponseDTO(savedAttempt);
    }

    @Override
    @Transactional
    public StudentAttemptResponseDTO getStudentAttempt(UUID attemptId) {
        StudentExamAttempt attempt = studentExamAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student attempt not found with id: " + attemptId));
        return convertToStudentAttemptResponseDTO(attempt);
    }

    @Override
    @Transactional
    public List<StudentAttemptResponseDTO> getStudentAttempts() {
        List<StudentExamAttempt> attempts = studentExamAttemptRepository.findAll();
        return attempts.stream()
                .map(this::convertToStudentAttemptResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<StudentAttemptResponseDTO> getStudentAttemptsByStudentIdAndExamId(UUID studentId, UUID examId) {
        // Validate that exam exists
        examRepository.findById(examId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Exam not found with id: " + examId));

        List<StudentExamAttempt> attempts = studentExamAttemptRepository.findByStudentIdAndExamId(studentId, examId);
        return attempts.stream()
                .map(this::convertToStudentAttemptResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<StudentAttemptResponseDTO> getStudentAttemptsByExamId(UUID examId) {
        // Validate that exam exists
        examRepository.findById(examId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Exam not found with id: " + examId));

        List<StudentExamAttempt> attempts = studentExamAttemptRepository.findByExamId(examId);
        return attempts.stream()
                .map(this::convertToStudentAttemptResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public StudentAnswerChoiceResponseDTO submitChoiceAnswer(UUID attemptId, StudentAnswerChoiceRequestDTO request) {
        // Validate that attempt exists
        StudentExamAttempt attempt = studentExamAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student attempt not found with id: " + attemptId));

        // Validate that question exists
        ExamQuestion question = examQuestionRepository.findById(request.getExamQuestionId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Question not found with id: " + request.getExamQuestionId()));

        // Validate that choice exists
        ExamQuestionChoice choice = examQuestionChoiceRepository.findById(request.getSelectedChoiceId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Choice not found with id: " + request.getSelectedChoiceId()));

        // Create new student answer choice entity
        StudentAnswerChoice studentAnswer = new StudentAnswerChoice();
        studentAnswer.setId(UUID.randomUUID());
        studentAnswer.setStudentExamAttempt(attempt);
        studentAnswer.setExamQuestion(question);
        studentAnswer.setSelectedChoice(choice);
        studentAnswer.setIsCorrect(choice.getIsCorrect());
        studentAnswer.setScore(choice.getMarkValue());
        studentAnswer.setCreatedAt(LocalDateTime.now());
        studentAnswer.setUpdatedAt(LocalDateTime.now());

        StudentAnswerChoice savedAnswer = studentAnswerChoiceRepository.save(studentAnswer);
        return convertToStudentAnswerChoiceResponseDTO(savedAnswer);
    }

    @Override
    @Transactional
    public StudentAnswerTextResponseDTO submitTextAnswers(UUID attemptId, StudentAnswerTextRequestDTO request) {
        // Validate that attempt exists
        StudentExamAttempt attempt = studentExamAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student attempt not found with id: " + attemptId));

        // Validate that question exists
        ExamQuestion question = examQuestionRepository.findById(request.getExamQuestionId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Question not found with id: " + request.getExamQuestionId()));

        List<ExamQuestionAnswerKey> answerKeys = examQuestionAnswerKeyRepository.findByExamQuestionId(request.getExamQuestionId());
        List<StudentAnswerTextRequestDTO.AnswerText> answerTexts = request.getAnswerTexts();

        if(answerKeys == null || answerTexts == null || answerKeys.isEmpty() || answerTexts.isEmpty()) return null;

        Map<Integer, ExamQuestionAnswerKey> examQuestionAnswerKeyMap = new HashMap<>();
        answerKeys.forEach(key -> {
            examQuestionAnswerKeyMap.put(key.getSortOrder(), key);
        });

        double markObtained = !examQuestionAnswerKeyMap.isEmpty() ? question.getMark() / examQuestionAnswerKeyMap.size() : 0;


        List<StudentAnswerText> studentAnswerTexts = new ArrayList<>();
        answerTexts.forEach(answerText -> {
            // Create new student answer text entity
            StudentAnswerText studentAnswer = new StudentAnswerText();
            studentAnswer.setId(UUID.randomUUID());
            studentAnswer.setStudentExamAttempt(attempt);
            studentAnswer.setExamQuestion(question);
            studentAnswer.setQuestionPart(answerText.getQuestionPart());
            studentAnswer.setStudentAnswer(answerText.getStudentAnswer());
            studentAnswer.setSortOrder(answerText.getSortOrder());

            ExamQuestionAnswerKey answerKey = examQuestionAnswerKeyMap.get(answerText.getSortOrder());

            boolean isCorrect = !QuestionType.Matching.name().equals(question.getQuestionType()) ||
                    !Utils.isEmpty(answerText.getQuestionPart()) && answerKey.getQuestionPart().equals(answerText.getQuestionPart());

            isCorrect = isCorrect && ((answerKey.isCaseSensitive()) ?
                    !Utils.isEmpty(answerText.getStudentAnswer()) && answerText.getStudentAnswer().equals(answerKey.getAnswerText()) :
                    !Utils.isEmpty(answerText.getStudentAnswer()) && answerText.getStudentAnswer().equalsIgnoreCase(answerKey.getAnswerText()));

            studentAnswer.setIsCorrect(isCorrect);


            studentAnswer.setMarkObtained(isCorrect ? markObtained : 0d);
            studentAnswer.setCreatedAt(LocalDateTime.now());
            studentAnswer.setUpdatedAt(LocalDateTime.now());
            studentAnswerTexts.add(studentAnswer);
        });

        List<StudentAnswerText> savedAnswer = studentAnswerTextRepository.saveAll(studentAnswerTexts);
        return convertToStudentAnswerTextResponseDTO(savedAnswer);
    }

    @Override
    @Transactional
    public StudentAnswerCodeResponseDTO submitCodeAnswer(UUID attemptId, StudentAnswerCodeRequestDTO request) {
        // Validate that attempt exists
        StudentExamAttempt attempt = studentExamAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student attempt not found with id: " + attemptId));

        // Validate that question exists
        ExamQuestion question = examQuestionRepository.findById(request.getExamQuestionId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Question not found with id: " + request.getExamQuestionId()));

        // Validate that programming language exists
        ProgrammingLanguage language = programmingLanguageRepository.findById(request.getLanguageId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Programming language not found with id: " + request.getLanguageId()));

        // Create new student answer code entity
        StudentAnswerCode studentAnswer = new StudentAnswerCode();
        studentAnswer.setId(UUID.randomUUID());
        studentAnswer.setStudentExamAttempt(attempt);
        studentAnswer.setExamQuestion(question);
        studentAnswer.setSubmittedCode(request.getSubmittedCode());
        studentAnswer.setLanguage(language);
        studentAnswer.setCreatedAt(LocalDateTime.now());
        studentAnswer.setUpdatedAt(LocalDateTime.now());

        StudentAnswerCode savedAnswer = studentAnswerCodeRepository.save(studentAnswer);
        return convertToStudentAnswerCodeResponseDTO(savedAnswer);
    }

    @Override
    @Transactional
    public List<StudentCodingTestResultResponseDTO> getCodingTestResults(UUID codeAnswerId) {
        // Validate that code answer exists
        studentAnswerCodeRepository.findById(codeAnswerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Code answer not found with id: " + codeAnswerId));

        List<StudentCodingTestResult> results = studentCodingTestResultRepository.findByStudentAnswerCodeId(codeAnswerId);
        return results.stream()
                .map(this::convertToStudentCodingTestResultResponseDTO)
                .collect(Collectors.toList());
    }

    // Helper method to convert ExamQuestion entity to QuestionResponseDTO
    private QuestionResponseDTO convertToQuestionResponseDTO(ExamQuestion question) {
        QuestionResponseDTO response = QuestionResponseDTO.createQuestionResponse(question.getQuestionType());
        
        // Set common fields
        response.setId(question.getId());
        response.setExamId(question.getExam().getId());
        response.setSectionId(question.getSection() != null ? question.getSection().getId() : null);
        response.setQuestionPoolId(question.getQuestionPool() != null ? question.getQuestionPool().getId() : null);
        response.setQuestionText(question.getQuestionText());
        response.setQuestionType(question.getQuestionType());
        response.setExplanation(question.getExplanation());
        response.setTimeLimit(question.getTimeLimit());
        response.setMemoryLimit(question.getMemoryLimit());
        response.setMark(question.getMark());
        response.setPosition(question.getPosition());
        response.setActive(question.isActive());

        // Set type-specific fields
        switch (QuestionType.valueOf(question.getQuestionType())) {
            case TF:
            case MCQ:
            case MultiChoice:
                ChoiceQuestionResponseDTO choiceDto = (ChoiceQuestionResponseDTO) response;
                List<ExamQuestionChoice> choices = examQuestionChoiceRepository.findByExamQuestion(question);
                choiceDto.setChoices(choices.stream()
                        .map(this::convertToChoiceResponseDTO)
                        .collect(Collectors.toList()));
                break;

            case Complete:
            case Matching:
                AnswerKeyQuestionResponseDTO answerDto = (AnswerKeyQuestionResponseDTO) response;
                List<ExamQuestionAnswerKey> answerKeys = examQuestionAnswerKeyRepository.findByExamQuestion(question);
                answerDto.setAnswerKeys(answerKeys.stream()
                        .map(this::convertToAnswerKeyResponseDTO)
                        .collect(Collectors.toList()));
                break;

            case Coding:
                CodingQuestionResponseDTO codingDto = (CodingQuestionResponseDTO) response;
                if (question.getProgrammingLanguage() != null) {
                    codingDto.setProgrammingLanguageId(question.getProgrammingLanguage().getId());
                }
                List<CodingTestCase> testCases = codingTestCaseRepository.findByExamQuestion(question);
                codingDto.setTestCases(testCases.stream()
                        .map(this::convertToCodingTestCaseResponseDTO)
                        .collect(Collectors.toList()));
                break;

            default:
                throw new IllegalStateException("Unsupported question type: " + question.getQuestionType());
        }

        return response;
    }

    // Helper method to convert ExamQuestionChoice entity to ChoiceResponseDTO
    private ChoiceResponseDTO convertToChoiceResponseDTO(ExamQuestionChoice choice) {
        ChoiceResponseDTO response = new ChoiceResponseDTO();
        response.setId(choice.getId());
        response.setChoiceText(choice.getChoiceText());
        response.setIsCorrect(choice.getIsCorrect());
        response.setMarkValue(choice.getMarkValue());
        return response;
    }

    // Helper method to convert ExamQuestionAnswerKey entity to AnswerKeyResponseDTO
    private AnswerKeyResponseDTO convertToAnswerKeyResponseDTO(ExamQuestionAnswerKey answerKey) {
        AnswerKeyResponseDTO response = new AnswerKeyResponseDTO();
        response.setId(answerKey.getId());
        response.setAnswerText(answerKey.getAnswerText());
        response.setQuestionPart(answerKey.getQuestionPart());
        response.setCaseSensitive(answerKey.isCaseSensitive());
        response.setSortOrder(answerKey.getSortOrder());
        return response;
    }

    // Helper method to convert CodingTestCase entity to CodingTestCaseResponseDTO
    private CodingTestCaseResponseDTO convertToCodingTestCaseResponseDTO(CodingTestCase testCase) {
        CodingTestCaseResponseDTO response = new CodingTestCaseResponseDTO();
        response.setId(testCase.getId());
        response.setExamQuestionId(testCase.getExamQuestion().getId());
        response.setInput(testCase.getInput());
        response.setExpectedOutput(testCase.getExpectedOutput());
        response.setMark(testCase.getMark());
        response.setSample(testCase.isSample());
        return response;
    }

    // Helper method to convert StudentExamAttempt entity to StudentAttemptResponseDTO
    private StudentAttemptResponseDTO convertToStudentAttemptResponseDTO(StudentExamAttempt attempt) {
        StudentAttemptResponseDTO response = new StudentAttemptResponseDTO();
        response.setId(attempt.getId());
        response.setStudentId(attempt.getStudent().getStudentId());
        response.setExamId(attempt.getExam().getId());
        response.setAttemptNumber(attempt.getAttemptNumber());
        response.setStartTime(attempt.getStartTime());
        response.setEndTime(attempt.getEndTime());
        response.setScore(attempt.getScore());
        response.setStatus(attempt.getStatus());
        return response;
    }

    // Helper method to convert StudentAnswerChoice entity to StudentAnswerChoiceResponseDTO
    private StudentAnswerChoiceResponseDTO convertToStudentAnswerChoiceResponseDTO(StudentAnswerChoice answer) {
        StudentAnswerChoiceResponseDTO response = new StudentAnswerChoiceResponseDTO();
        response.setId(answer.getId());
        response.setSelectedChoiceId(answer.getSelectedChoice() != null ? answer.getSelectedChoice().getId() : null);
        return response;
    }

    // Helper method to convert StudentAnswerText entity to StudentAnswerTextResponseDTO
    private StudentAnswerTextResponseDTO convertToStudentAnswerTextResponseDTO(List<StudentAnswerText> answers) {
        if(answers == null || answers.isEmpty()) return new StudentAnswerTextResponseDTO();

        StudentAnswerTextResponseDTO response = new StudentAnswerTextResponseDTO();
        response.setStudentExamAttemptId(answers.get(0).getStudentExamAttempt().getId());
        response.setExamQuestionId(answers.get(0).getExamQuestion().getId());
        response.setAnswerTexts(new ArrayList<>());

        answers.forEach(answer -> {
            response.getAnswerTexts().add(new StudentAnswerTextResponseDTO
                    .AnswerText(answer.getId(), answer.getQuestionPart(), answer.getStudentAnswer(), answer.getSortOrder()));
        });

        return response;
    }

    // Helper method to convert StudentAnswerCode entity to StudentAnswerCodeResponseDTO
    private StudentAnswerCodeResponseDTO convertToStudentAnswerCodeResponseDTO(StudentAnswerCode answer) {
        StudentAnswerCodeResponseDTO response = new StudentAnswerCodeResponseDTO();
        response.setId(answer.getId());
        response.setSubmittedCode(answer.getSubmittedCode());
        response.setLanguageId(answer.getLanguage() != null ? answer.getLanguage().getId() : null);
        return response;
    }

    // Helper method to convert StudentCodingTestResult entity to StudentCodingTestResultResponseDTO
    private StudentCodingTestResultResponseDTO convertToStudentCodingTestResultResponseDTO(StudentCodingTestResult result) {
        StudentCodingTestResultResponseDTO response = new StudentCodingTestResultResponseDTO();
        response.setId(result.getId());
        response.setStudentAnswerCodeId(result.getStudentAnswerCode() != null ? result.getStudentAnswerCode().getId() : null);
        response.setTestCaseId(result.getTestCase() != null ? result.getTestCase().getId() : null);
        response.setPassed(result.getPassed());
        response.setMarkObtained(result.getMarkObtained());
        response.setExecutionTimeMs(result.getExecutionTimeMs());
        response.setMemoryUsedKb(result.getMemoryUsedKb());
        response.setFeedback(result.getFeedback());
        return response;
    }

    // Helper class to keep track of questions with their sections
    private static class QuestionWithSection {
        final ExamQuestion question;
        final ExamSection section;

        QuestionWithSection(ExamQuestion question, ExamSection section) {
            this.question = question;
            this.section = section;
        }
    }
} 