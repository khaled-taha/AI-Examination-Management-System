package com.university.exam.examManagement.services;

import com.university.exam.academicManagement.entities.AcademicYear;
import com.university.exam.academicManagement.entities.AcademicYearCourse;
import com.university.exam.academicManagement.repos.AcademicYearCourseRepository;
import com.university.exam.academicManagement.repos.AcademicYearRepository;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    private final AcademicYearRepository academicYearRepository;
    private final AcademicYearCourseRepository academicYearCourseRepository;

    // Code evaluation integration service
    private final CodeEvaluationIntegrationService codeEvaluationIntegrationService;


    @Override
    public List<LanguagesResponseDTO> getAvailableLanguages() {
        return this.programmingLanguageRepository.findAll()
                .stream()
                .filter(ProgrammingLanguage::isEnabled)
                .map(LanguagesResponseDTO::fromEntity).toList();
    }

    @Override
    @Transactional
    public ExamResponseDTO saveExam(ExamRequestDTO request) {
        // Validate exam dates
        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Start date cannot be after end date");
        }

        // Validate related entities exist
        Admin creator = adminRepository.findById(request.getCreatorId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Admin not found with id: " + request.getCreatorId()));
        
        AcademicTerm term = academicTermRepository.findById(request.getTermId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Academic term not found with id: " + request.getTermId()));
        
        AcademicYear academicYear = academicYearRepository.findById(request.getAcademicYearId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Academic year not found with id: " + request.getAcademicYearId()));

        AcademicYearCourse academicYearCourse = academicYearCourseRepository.findById(request.getAcademicYearCourseId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Academic year Course not found with id: " + request.getAcademicYearCourseId()));

        if (LocalDateTime.now().isAfter(request.getEndDate())) {
            request.setStatus(ExamStatus.EXPIRED.name());
        }

        // Create new exam entity
        Exam exam = new Exam();
        exam.setId(request.getId());
        exam.setTitle(request.getTitle());
        exam.setDescription(request.getDescription());
        exam.setStartDate(request.getStartDate());
        exam.setEndDate(request.getEndDate());
        exam.setStatus(request.getStatus() != null ? request.getStatus() : "DRAFT");
        exam.setCreator(creator);
        exam.setAcademicYearCourse(academicYearCourse);
        exam.setTerm(term);
        exam.setAcademicYearGroup(academicYearGroupRepository.findByAcademicYearId(academicYear.getId()).get());
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

        List<Exam> updatedStatusExam = checkExamStatus(exams);
        if(!Utils.isEmpty(updatedStatusExam)) this.examRepository.saveAll(updatedStatusExam);

        return exams.stream()
                .map(this::convertToExamResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<ExamResponseDTO> getExamsByAcademicYearCourseId(UUID academicYearCourseId) {
        // Use the new repository method with @Query
        List<Exam> exams = examRepository.findExamsByAcademicYearCourse(academicYearCourseId);

        List<Exam> updatedStatusExam = checkExamStatus(exams);
        if(!Utils.isEmpty(updatedStatusExam)) this.examRepository.saveAll(updatedStatusExam);

        return exams.stream()
                .map(this::convertToExamResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Page<ExamResponseDTO> getExamsByAcademicYearCourseId(UUID academicYearCourseId, Pageable pageable) {
        Page<Exam> examsPage = examRepository.findExamsByAcademicYearCourse(academicYearCourseId, pageable);

        List<Exam> updatedStatusExam = checkExamStatus(examsPage.getContent());
        if (!Utils.isEmpty(updatedStatusExam)) this.examRepository.saveAll(updatedStatusExam);

        return examsPage.map(this::convertToExamResponseDTO);
    }


    private List<Exam> checkExamStatus(List<Exam> exams) {
        LocalDateTime now = LocalDateTime.now();

        return exams.stream()
                .filter(exam -> {
                    boolean isOngoing = !now.isBefore(exam.getStartDate()) && now.isBefore(exam.getEndDate());
                    String targetStatus = isOngoing ? ExamStatus.ACTIVE.name() : ExamStatus.EXPIRED.name();

                    if (!targetStatus.equalsIgnoreCase(exam.getStatus())) {
                        exam.setStatus(targetStatus);
                        return true;
                    }
                    return false;
                })
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
        response.setQuestionsPerPage(exam.getQuestionsPerPage());
        response.setShowResult(exam.isShowResult());
        response.setCreationTime(exam.getCreatedAt());
        return response;
    }

    @Override
    public SectionResponseDTO saveSection(UUID examId, SectionRequestDTO request) {
        // Validate that exam exists
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Exam not found with id: " + examId));

        // Create new section entity
        ExamSection section = new ExamSection();
        section.setId(request.getId());
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

    private ExamQuestionResponseDTO convertQuestionToPolymorphicDTO(ExamQuestion question) {
        ExamQuestionResponseDTO response = ExamQuestionResponseDTO.createQuestionResponse(question.getQuestionType());
        
        // Set common fields
        response.setId(question.getId());
        if (question.getExam() != null) {
            response.setExamId(question.getExam().getId());
        }
        if (question.getSection() != null) {
            response.setSectionId(question.getSection().getId());
        }
        if (question.getQuestionPool() != null) {
            response.setQuestionPoolId(question.getQuestionPool().getId());
        }
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
                ExamQuestionChoiceResponseDTO choiceDto = (ExamQuestionChoiceResponseDTO) response;
                List<ExamQuestionChoice> choices = examQuestionChoiceRepository.findByExamQuestion(question);
                choiceDto.setChoices(convertToExamQuestionChoicesResponseDTO(choices));
                break;

            case Complete:
                break;
            case Matching:
                ExamQuestionAnswerKeyResponseDTO answerDto = (ExamQuestionAnswerKeyResponseDTO) response;
                List<ExamQuestionAnswerKey> answerKeys = examQuestionAnswerKeyRepository.findByExamQuestion(question);
                answerDto.setAnswerKeys(convertToAnswerKeyResponseDTO(answerKeys));
                break;

            case Coding:
                ExamQuestionCodingResponseDTO codingDto = (ExamQuestionCodingResponseDTO) response;
                if (question.getProgrammingLanguage() != null) {
                    codingDto.setProgrammingLanguageId(question.getProgrammingLanguage().getId());
                }
                List<CodingTestCase> testCases = codingTestCaseRepository.findByExamQuestion(question);
                codingDto.setTestCases(convertToCodingTestCaseResponseDTO(testCases));
                break;

            default:
                throw new IllegalStateException("Unsupported question type: " + question.getQuestionType());
        }

        return response;
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
                // For answer key questions, students just see the question text
                // No additional data needed in student view
                break;

            case Matching:
                StudentAnswerKeyQuestionViewDTO matchingDto = (StudentAnswerKeyQuestionViewDTO) questionViewDTO;
                List<ExamQuestionAnswerKey> answerKeys = examQuestionAnswerKeyRepository.findByExamQuestion(question);
                matchingDto.setMatchingItems(answerKeys.stream()
                        .map(key -> new StudentAnswerKeyQuestionViewDTO.MatchingItem(key.getId(), key.getQuestionPart(), key.getSortOrder()))
                        .collect(Collectors.toList()));
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
    public ExamQuestionResponseDTO saveQuestion(UUID examId, QuestionRequestDTO request) {
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
        question.setId(request.getId());
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
    public ExamQuestionChoicesResponseDTO saveChoice(UUID questionId, ExamQuestionChoicesRequestDTO request) {
        // Validate that question exists
        ExamQuestion question = examQuestionRepository.findById(questionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Question not found with id: " + questionId));

        if(Utils.isEmpty(request.getChoices()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Empty Choices for the question : " + questionId);

        List<ExamQuestionChoice> choices = new ArrayList<>();
        request.getChoices().forEach(choice -> {
            ExamQuestionChoice savedChoice = new ExamQuestionChoice();
            savedChoice.setId(choice.getId());
            savedChoice.setExamQuestion(question);
            savedChoice.setChoiceText(choice.getChoiceText());
            savedChoice.setIsCorrect(choice.getIsCorrect());
            savedChoice.setMarkValue(choice.getMarkValue());
            choices.add(savedChoice);
        });

        List<ExamQuestionChoice> savedChoice = examQuestionChoiceRepository.saveAll(choices);
        return convertToExamQuestionChoicesResponseDTO(savedChoice);
    }

    @Override
    @Transactional
    public ExamQuestionChoicesResponseDTO getChoices(UUID questionId) {
        // Validate that question exists
        examQuestionRepository.findById(questionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Question not found with id: " + questionId));

        List<ExamQuestionChoice> choices = examQuestionChoiceRepository.findByExamQuestionId(questionId);
        return convertToExamQuestionChoicesResponseDTO(choices);
    }

    @Override
    @Transactional
    public ExamQuestionAnswerKeysResponseDTO saveAnswerKey(UUID questionId, ExamQuestionAnswerKeyRequestDTO request) {
        // Validate that question exists
        ExamQuestion question = examQuestionRepository.findById(questionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Question not found with id: " + questionId));

        if(Utils.isEmpty(request.getAnswerKeys()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Empty Keys for the question : " + questionId);

        List<ExamQuestionAnswerKey> keys = new ArrayList<>();
        request.getAnswerKeys().forEach(key -> {
            ExamQuestionAnswerKey answerKey = new ExamQuestionAnswerKey();
            answerKey.setId(key.getId());
            answerKey.setExamQuestion(question);
            answerKey.setAnswerText(key.getAnswerText());
            answerKey.setQuestionPart(key.getQuestionPart());
            answerKey.setCaseSensitive(key.isCaseSensitive());
            answerKey.setSortOrder(key.getSortOrder());
            keys.add(answerKey);
        });

        List<ExamQuestionAnswerKey> savedAnswerKey = examQuestionAnswerKeyRepository.saveAll(keys);
        return convertToAnswerKeyResponseDTO(savedAnswerKey);
    }

    @Override
    @Transactional
    public ExamQuestionAnswerKeysResponseDTO getAnswerKeys(UUID questionId) {
        // Validate that question exists
        examQuestionRepository.findById(questionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Question not found with id: " + questionId));

        List<ExamQuestionAnswerKey> answerKeys = examQuestionAnswerKeyRepository.findByExamQuestionId(questionId);
        return convertToAnswerKeyResponseDTO(answerKeys);
    }

    @Override
    @Transactional
    public ExamQuestionCodingTestCaseResponseDTO saveTestCase(UUID questionId, ExamQuestionCodingTestCaseRequestDTO request) {
        // Validate that question exists
       ExamQuestion examQuestion = examQuestionRepository.findById(questionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Question not found with id: " + questionId));

        if(Utils.isEmpty(request.getTestCases()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Empty Test Cases for the question : " + questionId);

        List<CodingTestCase> testCases = new ArrayList<>();
        System.out.println(request.getTestCases());
        request.getTestCases().forEach(testCase -> {
            CodingTestCase savedTestCase = new CodingTestCase();
            savedTestCase.setId(testCase.getId());
            savedTestCase.setExamQuestion(examQuestion);
            savedTestCase.setInput(testCase.getInput());
            savedTestCase.setExpectedOutput(testCase.getExpectedOutput());
            savedTestCase.setMark(testCase.getMark());
            savedTestCase.setSample(testCase.isSample());
            testCases.add(savedTestCase);
        });

        List<CodingTestCase> savedTestCase = codingTestCaseRepository.saveAll(testCases);
        return convertToCodingTestCaseResponseDTO(savedTestCase);
    }

    @Override
    @Transactional
    public ExamQuestionCodingTestCaseResponseDTO getTestCases(UUID questionId) {
        // Validate that question exists
        examQuestionRepository.findById(questionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Question not found with id: " + questionId));

        List<CodingTestCase> testCases = codingTestCaseRepository.findByExamQuestionId(questionId);
        return convertToCodingTestCaseResponseDTO(testCases);
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
    public StudentAnswerChoicesResponseDTO submitChoiceAnswer(UUID attemptId, List<StudentAnswerChoiceRequestDTO> requests) {

        if(Utils.isEmpty(requests)) throw  new ResponseStatusException(HttpStatus.BAD_REQUEST, "Trying to save Empty Question Answers!");

        // Validate that attempt exists
        StudentExamAttempt attempt = studentExamAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student attempt not found with id: " + attemptId));

        if (attempt.getEndTime() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Exam attempt already ended");
        }

        List<StudentAnswerChoice> choices = requests.stream().map(request -> {

            // Validate that question exists
            ExamQuestion question = examQuestionRepository.findById(request.getExamQuestionId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Question not found with id: " + request.getExamQuestionId()));

            // Validate that choice exists
            ExamQuestionChoice choice = examQuestionChoiceRepository.findById(request.getSelectedChoiceId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Choice not found with id: " + request.getSelectedChoiceId()));

            // Create new student answer choice entity
            StudentAnswerChoice studentAnswer = new StudentAnswerChoice();
            studentAnswer.setId(request.getId());
            studentAnswer.setStudentExamAttempt(attempt);
            studentAnswer.setExamQuestion(question);
            studentAnswer.setSelectedChoice(choice);
            studentAnswer.setIsCorrect(choice.getIsCorrect());
            studentAnswer.setScore(choice.getMarkValue());
            return studentAnswer;
        }).toList();

        List<StudentAnswerChoice> savedAnswer = studentAnswerChoiceRepository.saveAll(choices);
        return convertToStudentAnswerChoiceResponseDTO(savedAnswer);
    }

    @Override
    @Transactional
    public StudentAnswerTextResponseDTO submitTextAnswers(UUID attemptId, List<StudentAnswerTextRequestDTO> requests) {

        if(Utils.isEmpty(requests)) throw  new ResponseStatusException(HttpStatus.BAD_REQUEST, "Trying to save Empty Question Answers!");

        // Validate that attempt exists
        StudentExamAttempt attempt = studentExamAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student attempt not found with id: " + attemptId));

        if (attempt.getEndTime() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Exam attempt already ended");
        }

        List<StudentAnswerText> savedStudentAnswerTexts = requests.stream().flatMap(request -> {

            // Validate that question exists
            ExamQuestion question = examQuestionRepository.findById(request.getExamQuestionId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Question not found with id: " + request.getExamQuestionId()));

            List<ExamQuestionAnswerKey> answerKeys = examQuestionAnswerKeyRepository.findByExamQuestionId(request.getExamQuestionId());
            List<StudentAnswerTextRequestDTO.AnswerText> answerTexts = request.getAnswerTexts();

            if(Utils.isEmpty(answerKeys) || Utils.isEmpty(answerTexts)) return Stream.empty();

            Map<Integer, ExamQuestionAnswerKey> examQuestionAnswerKeyMap = new HashMap<>();
            answerKeys.forEach(key -> {
                examQuestionAnswerKeyMap.put(key.getSortOrder(), key);
            });

            double markObtained = !examQuestionAnswerKeyMap.isEmpty() ? question.getMark() / examQuestionAnswerKeyMap.size() : 0;

            return answerTexts.stream().map(answerText -> {
                // Create new student answer text entity
                StudentAnswerText studentAnswer = new StudentAnswerText();
                studentAnswer.setId(answerText.getId());
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
                return studentAnswer;
            });

        }).toList();



        List<StudentAnswerText> savedAnswer = studentAnswerTextRepository.saveAll(savedStudentAnswerTexts);
        return convertToStudentAnswerTextResponseDTO(savedAnswer);
    }

    @Override
    @Transactional
    public StudentAnswerCodeResponseDTO submitCodeAnswer(UUID attemptId, List<StudentAnswerCodeRequestDTO> requests) {

        if(Utils.isEmpty(requests)) throw  new ResponseStatusException(HttpStatus.BAD_REQUEST, "Trying to save Empty Question Answers!");

        // Validate that attempt exists
        StudentExamAttempt attempt = studentExamAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student attempt not found with id: " + attemptId));

        if (attempt.getEndTime() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Exam attempt already ended");
        }

        List<StudentAnswerCode> studentAnswerCodes = requests.stream().map(request -> {

            // Validate that question exists
            ExamQuestion question = examQuestionRepository.findById(request.getExamQuestionId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Question not found with id: " + request.getExamQuestionId()));

            // Validate that programming language exists
            ProgrammingLanguage language = programmingLanguageRepository.findById(request.getLanguageId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Programming language not found with id: " + request.getLanguageId()));

            // Check if this is a coding question
            if (!QuestionType.Coding.name().equals(question.getQuestionType())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Question is not a coding question: " + request.getExamQuestionId());
            }

            // Create new student answer code entity
            StudentAnswerCode studentAnswer = new StudentAnswerCode();
            studentAnswer.setId(request.getId());
            studentAnswer.setStudentExamAttempt(attempt);
            studentAnswer.setExamQuestion(question);
            studentAnswer.setSubmittedCode(request.getSubmittedCode());
            studentAnswer.setLanguage(language);

            // Set initial status - evaluation will be done at exam end
            studentAnswer.setTotalScore(0.0);
            studentAnswer.setResultSummary("Code submitted - evaluation pending");

            return studentAnswer;
        }).toList();

        List<StudentAnswerCode> savedAnswers = studentAnswerCodeRepository.saveAll(studentAnswerCodes);

        return convertToStudentAnswerCodeResponseDTO(savedAnswers);
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
    private ExamQuestionResponseDTO convertToQuestionResponseDTO(ExamQuestion question) {
        ExamQuestionResponseDTO response = ExamQuestionResponseDTO.createQuestionResponse(question.getQuestionType());
        
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
                ExamQuestionChoiceResponseDTO choiceDto = (ExamQuestionChoiceResponseDTO) response;
                List<ExamQuestionChoice> choices = examQuestionChoiceRepository.findByExamQuestion(question);
                choiceDto.setChoices(convertToExamQuestionChoicesResponseDTO(choices));
                break;

            case Complete:
                break;
            case Matching:
                ExamQuestionAnswerKeyResponseDTO answerDto = (ExamQuestionAnswerKeyResponseDTO) response;
                List<ExamQuestionAnswerKey> answerKeys = examQuestionAnswerKeyRepository.findByExamQuestion(question);
                answerDto.setAnswerKeys(convertToAnswerKeyResponseDTO(answerKeys));
                break;

            case Coding:
                ExamQuestionCodingResponseDTO codingDto = (ExamQuestionCodingResponseDTO) response;
                if (question.getProgrammingLanguage() != null) {
                    codingDto.setProgrammingLanguageId(question.getProgrammingLanguage().getId());
                }
                List<CodingTestCase> testCases = codingTestCaseRepository.findByExamQuestion(question);
                codingDto.setTestCases(convertToCodingTestCaseResponseDTO(testCases));
                break;

            default:
                throw new IllegalStateException("Unsupported question type: " + question.getQuestionType());
        }

        return response;
    }

    // Helper method to convert ExamQuestionChoice entity to ChoiceResponseDTO
    private ExamQuestionChoicesResponseDTO convertToExamQuestionChoicesResponseDTO(List<ExamQuestionChoice> choices) {
        ExamQuestionChoicesResponseDTO response = new ExamQuestionChoicesResponseDTO();
        if(Utils.isEmpty(choices)) return response;

        response.setChoices(new ArrayList<>());
        response.setExamQuestionId(choices.get(0).getExamQuestion().getId());

        choices.forEach(choice -> {
            ExamQuestionChoicesResponseDTO.Choice savedChoice = new ExamQuestionChoicesResponseDTO.Choice();
            savedChoice.setId(choice.getId());
            savedChoice.setChoiceText(choice.getChoiceText());
            savedChoice.setIsCorrect(choice.getIsCorrect());
            savedChoice.setMarkValue(choice.getMarkValue());
            response.getChoices().add(savedChoice);
        });
        return response;
    }

    // Helper method to convert ExamQuestionAnswerKey entity to AnswerKeyResponseDTO
    private ExamQuestionAnswerKeysResponseDTO convertToAnswerKeyResponseDTO(List<ExamQuestionAnswerKey> answerKeys) {
        ExamQuestionAnswerKeysResponseDTO response = new ExamQuestionAnswerKeysResponseDTO();
        if(Utils.isEmpty(answerKeys)) return response;
        response.setAnswerKeys(new ArrayList<>());
        response.setExamQuestionId(answerKeys.get(0).getExamQuestion().getId());

        answerKeys.forEach(key -> {
            ExamQuestionAnswerKeysResponseDTO.AnswerKey savedAnswerKey = new ExamQuestionAnswerKeysResponseDTO.AnswerKey();
            savedAnswerKey.setId(key.getId());
            savedAnswerKey.setAnswerText(key.getAnswerText());
            savedAnswerKey.setQuestionPart(key.getQuestionPart());
            savedAnswerKey.setCaseSensitive(key.isCaseSensitive());
            savedAnswerKey.setSortOrder(key.getSortOrder());
            response.getAnswerKeys().add(savedAnswerKey);
        });
        return response;
    }

    // Helper method to convert CodingTestCase entity to CodingTestCaseResponseDTO
    private ExamQuestionCodingTestCaseResponseDTO convertToCodingTestCaseResponseDTO(List<CodingTestCase> testCases) {
        ExamQuestionCodingTestCaseResponseDTO response = new ExamQuestionCodingTestCaseResponseDTO();
        if(Utils.isEmpty(testCases)) return response;
        response.setTestCases(new ArrayList<>());
        response.setExamQuestionId(testCases.get(0).getExamQuestion().getId());

        testCases.forEach(testCase -> {
            ExamQuestionCodingTestCaseResponseDTO.TestCase savedTestCase = new ExamQuestionCodingTestCaseResponseDTO.TestCase();
            savedTestCase.setId(testCase.getId());
            savedTestCase.setInput(testCase.getInput());
            savedTestCase.setExpectedOutput(testCase.getExpectedOutput());
            savedTestCase.setMark(testCase.getMark());
            savedTestCase.setSample(testCase.isSample());
            response.getTestCases().add(savedTestCase);
        });

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
    private StudentAnswerChoicesResponseDTO convertToStudentAnswerChoiceResponseDTO(List<StudentAnswerChoice> answers) {
        if(Utils.isEmpty(answers)) return new StudentAnswerChoicesResponseDTO();

        StudentAnswerChoicesResponseDTO response = new StudentAnswerChoicesResponseDTO();
        response.setStudentExamAttemptId(answers.get(0).getStudentExamAttempt().getId());

        List<StudentAnswerChoicesResponseDTO.Answer> answerList = answers.stream().map(answer -> {
            StudentAnswerChoicesResponseDTO.Answer studentAnswer = new StudentAnswerChoicesResponseDTO.Answer();
            studentAnswer.setId(answer.getId());
            studentAnswer.setSelectedChoiceId(answer.getSelectedChoice() != null ? answer.getSelectedChoice().getId() : null);
            studentAnswer.setExamQuestionId(answer.getExamQuestion().getId());
            return studentAnswer;
        }).toList();

        response.setAnswers(answerList);
        return response;
    }

    // Helper method to convert StudentAnswerText entity to StudentAnswerTextResponseDTO
    private StudentAnswerTextResponseDTO convertToStudentAnswerTextResponseDTO(List<StudentAnswerText> answers) {
        if(Utils.isEmpty(answers)) return new StudentAnswerTextResponseDTO();

        StudentAnswerTextResponseDTO response = new StudentAnswerTextResponseDTO();
        response.setStudentExamAttemptId(answers.get(0).getStudentExamAttempt().getId());

        Map<String, List<StudentAnswerText>> examQuestionListMap = answers.stream().collect(Collectors.groupingBy(
                answer -> answer.getExamQuestion().getId().toString(),
                Collectors.toList()
        ));

        response.setStudentQuestionAnswerTexts(answers.stream().map(answer -> {
            StudentAnswerTextResponseDTO.StudentQuestionAnswerText studentQuestionAnswerText = new StudentAnswerTextResponseDTO.StudentQuestionAnswerText();
            studentQuestionAnswerText.setExamQuestionId(answer.getExamQuestion().getId());

            List<StudentAnswerText> answerTexts = examQuestionListMap.getOrDefault(answer.getExamQuestion().getId().toString(), List.of());

            studentQuestionAnswerText.setAnswerTexts(answerTexts.stream().map(answerText -> new StudentAnswerTextResponseDTO.StudentQuestionAnswerText
                    .AnswerText(answerText.getId(), answerText.getQuestionPart(), answerText.getStudentAnswer(), answerText.getSortOrder())).toList());

            return studentQuestionAnswerText;
        }).toList());

        return response;
    }

    // Helper method to convert StudentAnswerCode entity to StudentAnswerCodeResponseDTO
    private StudentAnswerCodeResponseDTO convertToStudentAnswerCodeResponseDTO(List<StudentAnswerCode> answers) {
        if(Utils.isEmpty(answers)) return new StudentAnswerCodeResponseDTO();
        StudentAnswerCodeResponseDTO response = new StudentAnswerCodeResponseDTO();
        response.setStudentExamAttemptId(answers.get(0).getStudentExamAttempt().getId());
        response.setAnswers(answers.stream().map(answer -> {
            StudentAnswerCodeResponseDTO.CodeAnswer codeAnswer = new StudentAnswerCodeResponseDTO.CodeAnswer();
            codeAnswer.setId(answer.getId());
            codeAnswer.setSubmittedCode(answer.getSubmittedCode());
            codeAnswer.setLanguageId(answer.getLanguage() != null ? answer.getLanguage().getId() : null);
            codeAnswer.setExamQuestionId(answer.getExamQuestion().getId());
            return codeAnswer;
        }).toList());

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

    @Override
    @Transactional
    public void deleteChoice(UUID questionId, UUID choiceId) {
        examQuestionRepository.findById(questionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Question not found with id: " + questionId));
        ExamQuestionChoice choice = examQuestionChoiceRepository.findById(choiceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Choice not found with id: " + choiceId));
        if (!choice.getExamQuestion().getId().equals(questionId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Choice does not belong to the specified question");
        }
        examQuestionChoiceRepository.delete(choice);
    }

    @Override
    @Transactional
    public void deleteAnswerKey(UUID questionId, UUID answerKeyId) {
        examQuestionRepository.findById(questionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Question not found with id: " + questionId));
        ExamQuestionAnswerKey answerKey = examQuestionAnswerKeyRepository.findById(answerKeyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Answer key not found with id: " + answerKeyId));
        if (!answerKey.getExamQuestion().getId().equals(questionId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Answer key does not belong to the specified question");
        }
        examQuestionAnswerKeyRepository.delete(answerKey);
    }

    @Override
    @Transactional
    public void deleteTestCase(UUID questionId, UUID testCaseId) {
        examQuestionRepository.findById(questionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Question not found with id: " + questionId));
        CodingTestCase testCase = codingTestCaseRepository.findById(testCaseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Test case not found with id: " + testCaseId));
        if (!testCase.getExamQuestion().getId().equals(questionId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Test case does not belong to the specified question");
        }
        codingTestCaseRepository.delete(testCase);
    }

    @Override
    @Async
    @Transactional
    public CompletableFuture<Boolean> endExam(UUID attemptId) {
        StudentExamAttempt attempt = studentExamAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student attempt not found with id: " + attemptId));
        if (attempt.getEndTime() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Exam attempt already ended");
        }

        // Get all code answers for this attempt
        List<StudentAnswerCode> codeAnswers = studentAnswerCodeRepository.findByStudentExamAttempt(attempt);

        // Trigger async code evaluation for all coding answers
        for (StudentAnswerCode codeAnswer : codeAnswers) {
            triggerCodeEvaluation(codeAnswer);
        }

        // Calculate final score (sum of all answers' marks for this attempt)
        double successPercentage = attempt.getExam().getSuccessPercentage();
        double examMark = getExamTotalPoints(attempt.getExam().getId());
        double totalScore = 0d;

        // Choice answers
        List<StudentAnswerChoice> choiceAnswers = studentAnswerChoiceRepository.findByStudentExamAttempt(attempt);
        totalScore += choiceAnswers.stream().mapToDouble(a -> a.getScore() != null ? a.getScore() : 0d).sum();

        // Text answers
        List<StudentAnswerText> textAnswers = studentAnswerTextRepository.findByStudentExamAttempt(attempt);
        totalScore += textAnswers.stream().mapToDouble(a -> a.getMarkObtained() != null ? a.getMarkObtained() : 0d).sum();

        // Code answers (initial score, will be updated after evaluation)
        totalScore += codeAnswers.stream().mapToDouble(a -> a.getTotalScore() != null ? a.getTotalScore() : 0d).sum();

        attempt.setEndTime(LocalDateTime.now());
        attempt.setScore(totalScore);
        attempt.setStatus((totalScore / examMark) * 100 >= successPercentage ? "SUCCESS" : "FAILED");
        attempt.setUpdatedAt(LocalDateTime.now());
        studentExamAttemptRepository.save(attempt);

        return CompletableFuture.completedFuture(true);
    }

    @Override
    @Transactional(readOnly = true)
    public double getExamTotalPoints(UUID examId) {
        // Validate that exam exists
        examRepository.findById(examId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Exam not found with id: " + examId));
        List<ExamQuestion> questions = examQuestionRepository.findByExamId(examId);
        return questions.stream().mapToDouble(ExamQuestion::getMark).sum();
    }

    @Override
    public StudentAnswerChoicesResponseDTO getStudentChoiceAnswer(UUID attemptId, UUID questionId) {
        return studentAnswerChoiceRepository.findByStudentExamAttemptIdAndExamQuestionId(attemptId, questionId)
                .map(entity -> {
                    StudentAnswerChoicesResponseDTO dto = new StudentAnswerChoicesResponseDTO();
                    dto.setStudentExamAttemptId(entity.getStudentExamAttempt().getId());
                    dto.setAnswers(List.of(new StudentAnswerChoicesResponseDTO.Answer(entity.getId(), entity.getExamQuestion().getId(),
                            entity.getSelectedChoice() != null ? entity.getSelectedChoice().getId() : null)));
                    return dto;
                })
                .orElse(null);
    }

    @Override
    public StudentAnswerTextResponseDTO getStudentTextAnswers(UUID attemptId, UUID questionId) {
        List<StudentAnswerText> answers = studentAnswerTextRepository.findByStudentExamAttemptIdAndExamQuestionId(attemptId, questionId);
        StudentAnswerTextResponseDTO dto = new StudentAnswerTextResponseDTO();
        dto.setStudentExamAttemptId(attemptId);
        dto.setStudentQuestionAnswerTexts(List.of(new StudentAnswerTextResponseDTO.StudentQuestionAnswerText(
                questionId,
                answers.stream().map(answer ->
                        new StudentAnswerTextResponseDTO.StudentQuestionAnswerText.AnswerText(
                                answer.getId(),
                                answer.getQuestionPart(),
                                answer.getStudentAnswer(),
                                answer.getSortOrder()
                        )
                ).toList()
        )));
        return dto;
    }

    @Override
    public StudentAnswerCodeResponseDTO getStudentCodeAnswer(UUID attemptId, UUID questionId) {
        return studentAnswerCodeRepository.findByStudentExamAttemptIdAndExamQuestionId(attemptId, questionId)
                .map(code -> {
                    StudentAnswerCodeResponseDTO dto = new StudentAnswerCodeResponseDTO();
                    dto.setStudentExamAttemptId(code.getStudentExamAttempt().getId());
                    dto.setAnswers(List.of(new StudentAnswerCodeResponseDTO.CodeAnswer(code.getId(),
                            code.getExamQuestion().getId(),
                            code.getSubmittedCode(),
                            code.getLanguage() != null ? code.getLanguage().getId() : null)));
                    return dto;
                })
                .orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public StudentExamResultResponseDTO getStudentExamResult(UUID attemptId) {
        // Get student attempt
        StudentExamAttempt attempt = studentExamAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student attempt not found with id: " + attemptId));

        // Get exam details
        Exam exam = attempt.getExam();
        
        // Calculate total exam score
        List<ExamQuestion> examQuestions = examQuestionRepository.findByExam(exam);
        double totalExamScore = examQuestions.stream().mapToDouble(ExamQuestion::getMark).sum();
        
        // Get student score from attempt
        double totalStudentScore = attempt.getScore() != null ? attempt.getScore() : 0.0;
        
        // Calculate percentage
        double percentage = totalExamScore > 0 ? (totalStudentScore / totalExamScore) * 100 : 0.0;
        
        // Determine attempt status
        String attemptStatus = percentage >= exam.getSuccessPercentage() ? "SUCCESS" : "FAILED";
        
        // Group questions by section
        Map<ExamSection, List<ExamQuestion>> questionsBySection = examQuestions.stream()
                .collect(Collectors.groupingBy(ExamQuestion::getSection));
        
        // Build section results
        List<StudentExamResultResponseDTO.SectionResultDTO> sectionResults = new ArrayList<>();
        
        for (Map.Entry<ExamSection, List<ExamQuestion>> entry : questionsBySection.entrySet()) {
            ExamSection section = entry.getKey();
            List<ExamQuestion> sectionQuestions = entry.getValue();
            
            StudentExamResultResponseDTO.SectionResultDTO sectionResult = new StudentExamResultResponseDTO.SectionResultDTO();
            sectionResult.setSectionId(section.getId());
            sectionResult.setSectionTitle(section.getTitle());
            sectionResult.setSectionPosition(section.getPosition());
            
            List<StudentExamResultResponseDTO.QuestionResultDTO> questionResults = new ArrayList<>();
            
            for (ExamQuestion question : sectionQuestions) {
                StudentExamResultResponseDTO.QuestionResultDTO questionResult = new StudentExamResultResponseDTO.QuestionResultDTO();
                questionResult.setQuestionId(question.getId());
                questionResult.setQuestionText(question.getQuestionText());
                questionResult.setQuestionType(question.getQuestionType());
                questionResult.setExplanation(question.getExplanation());
                questionResult.setQuestionScore(question.getMark());
                
                // Get student answer and score based on question type
                switch (QuestionType.valueOf(question.getQuestionType())) {
                    case TF:
                    case MCQ:
                    case MultiChoice:
                        StudentAnswerChoice choiceAnswer = studentAnswerChoiceRepository
                                .findByStudentExamAttemptIdAndExamQuestionId(attemptId, question.getId())
                                .orElse(null);
                        if (choiceAnswer != null) {
                            questionResult.setStudentAnswer(choiceAnswer.getSelectedChoice() != null ? 
                                    choiceAnswer.getSelectedChoice().getChoiceText() : "No answer");
                            questionResult.setStudentScore(choiceAnswer.getScore() != null ? choiceAnswer.getScore() : 0.0);
                            questionResult.setCorrect(choiceAnswer.getIsCorrect() != null ? choiceAnswer.getIsCorrect() : false);
                        } else {
                            questionResult.setStudentAnswer("No answer");
                            questionResult.setStudentScore(0.0);
                            questionResult.setCorrect(false);
                        }
                        break;
                        
                    case Complete:
                        List<StudentAnswerText> completeAnswers = studentAnswerTextRepository
                                .findByStudentExamAttemptIdAndExamQuestionId(attemptId, question.getId());
                        if (!completeAnswers.isEmpty()) {
                            double totalCompleteScore = completeAnswers.stream()
                                    .mapToDouble(answer -> answer.getMarkObtained() != null ? answer.getMarkObtained() : 0.0)
                                    .sum();
                            questionResult.setStudentScore(totalCompleteScore);
                            questionResult.setCorrect(totalCompleteScore == question.getMark());
                            
                            // For Complete questions, combine all answers (questionPart is null)
                            String combinedCompleteAnswer = completeAnswers.stream()
                                    .map(StudentAnswerText::getStudentAnswer)
                                    .collect(Collectors.joining("; "));
                            questionResult.setStudentAnswer(combinedCompleteAnswer);
                        } else {
                            questionResult.setStudentAnswer("No answer");
                            questionResult.setStudentScore(0.0);
                            questionResult.setCorrect(false);
                        }
                        break;
                        
                    case Matching:
                        List<StudentAnswerText> matchingAnswers = studentAnswerTextRepository
                                .findByStudentExamAttemptIdAndExamQuestionId(attemptId, question.getId());
                        if (!matchingAnswers.isEmpty()) {
                            double totalMatchingScore = matchingAnswers.stream()
                                    .mapToDouble(answer -> answer.getMarkObtained() != null ? answer.getMarkObtained() : 0.0)
                                    .sum();
                            questionResult.setStudentScore(totalMatchingScore);
                            questionResult.setCorrect(totalMatchingScore == question.getMark());
                            
                            // For Matching questions, create detailed matching answers (questionPart is not null)
                            List<StudentExamResultResponseDTO.MatchingAnswerDTO> matchingAnswerDTOs = matchingAnswers.stream()
                                    .map(answer -> {
                                        StudentExamResultResponseDTO.MatchingAnswerDTO matchingAnswer = new StudentExamResultResponseDTO.MatchingAnswerDTO();
                                        matchingAnswer.setQuestionPart(answer.getQuestionPart());
                                        matchingAnswer.setStudentAnswer(answer.getStudentAnswer());
                                        matchingAnswer.setSortOrder(answer.getSortOrder());
                                        matchingAnswer.setCorrect(answer.getIsCorrect() != null ? answer.getIsCorrect() : false);
                                        matchingAnswer.setScore(answer.getMarkObtained() != null ? answer.getMarkObtained() : 0.0);
                                        return matchingAnswer;
                                    })
                                    .collect(Collectors.toList());
                            questionResult.setMatchingAnswers(matchingAnswerDTOs);
                        } else {
                            questionResult.setStudentScore(0.0);
                            questionResult.setCorrect(false);
                            questionResult.setMatchingAnswers(new ArrayList<>());
                        }
                        break;
                        
                    case Coding:
                        StudentAnswerCode codeAnswer = studentAnswerCodeRepository
                                .findByStudentExamAttemptIdAndExamQuestionId(attemptId, question.getId())
                                .orElse(null);
                        if (codeAnswer != null) {
                            questionResult.setStudentAnswer(codeAnswer.getSubmittedCode());
                            questionResult.setStudentScore(codeAnswer.getTotalScore() != null ? codeAnswer.getTotalScore() : 0.0);
                            questionResult.setCorrect(codeAnswer.getTotalScore() != null && codeAnswer.getTotalScore() == question.getMark());
                            questionResult.setFeedback(codeAnswer.getResultSummary());
                        } else {
                            questionResult.setStudentAnswer("No code submitted");
                            questionResult.setStudentScore(0.0);
                            questionResult.setCorrect(false);
                        }
                        break;
                        
                    default:
                        questionResult.setStudentAnswer("Unsupported question type");
                        questionResult.setStudentScore(0.0);
                        questionResult.setCorrect(false);
                        break;
                }
                
                questionResults.add(questionResult);
            }
            
            sectionResult.setQuestionResults(questionResults);
            sectionResults.add(sectionResult);
        }
        
        // Build and return result
        StudentExamResultResponseDTO result = new StudentExamResultResponseDTO();
        result.setAttemptId(attemptId);
        result.setExamId(exam.getId());
        result.setExamTitle(exam.getTitle());
        result.setTotalExamScore(totalExamScore);
        result.setTotalStudentScore(totalStudentScore);
        result.setAttemptStatus(attemptStatus);
        result.setPercentage(percentage);
        result.setSectionResults(sectionResults);
        
        return result;
    }

    /**
     * Trigger async code evaluation for a student answer
     * @param studentAnswerCode The student's code answer to evaluate
     */
    private void triggerCodeEvaluation(StudentAnswerCode studentAnswerCode) {
        try {
            // Get test cases for the question
            List<CodingTestCase> testCases = codingTestCaseRepository.findByExamQuestion(studentAnswerCode.getExamQuestion());

            if (testCases.isEmpty()) {
                studentAnswerCode.setResultSummary("No test cases available for evaluation");
                studentAnswerCodeRepository.save(studentAnswerCode);
                return;
            }

            // Check if code evaluation service is available
            if (!codeEvaluationIntegrationService.isCodeEvaluationServiceAvailable()) {
                studentAnswerCode.setResultSummary("Code evaluation service is not available");
                studentAnswerCodeRepository.save(studentAnswerCode);
                return;
            }

            // Trigger async evaluation
            codeEvaluationIntegrationService.evaluateCodeAsync(
                studentAnswerCode,
                studentAnswerCode.getExamQuestion(),
                testCases,
                studentAnswerCode.getLanguage()
            );


        } catch (Exception e) {
            studentAnswerCode.setResultSummary("Error triggering evaluation: " + e.getMessage());
            studentAnswerCodeRepository.save(studentAnswerCode);
        }
    }
}