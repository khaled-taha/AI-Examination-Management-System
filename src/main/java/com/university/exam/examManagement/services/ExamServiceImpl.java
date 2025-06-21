package com.university.exam.examManagement.services;

import com.university.exam.examManagement.dtos.request.*;
import com.university.exam.examManagement.dtos.response.*;
import com.university.exam.examManagement.entities.*;
import com.university.exam.examManagement.repos.*;
import com.university.exam.academicManagement.entities.AcademicTerm;
import com.university.exam.academicManagement.entities.AcademicYearGroup;
import com.university.exam.userManagement.entities.Admin;
import com.university.exam.userManagement.entities.Student;
import com.university.exam.academicManagement.repos.AcademicTermRepository;
import com.university.exam.academicManagement.repos.AcademicYearGroupRepository;
import com.university.exam.userManagement.repos.AdminRepository;
import com.university.exam.userManagement.repos.StudentRepository;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
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

    @Override
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

        // Create new exam entity
        Exam exam = new Exam();
        exam.setId(UUID.randomUUID());
        exam.setTitle(request.getTitle());
        exam.setDescription(request.getDescription());
        exam.setStartDate(request.getStartDate());
        exam.setEndDate(request.getEndDate());
        exam.setStatus(request.getStatus() != null ? request.getStatus() : "DRAFT");
        exam.setCreator(creator);
        exam.setCourseCode(request.getCourseCode());
        exam.setTerm(term);
        exam.setAcademicYearGroup(academicYearGroup);
        exam.setSuccessPercentage(request.getSuccessPercentage());
        exam.setAllowedAttemptTimes(request.getAllowedAttemptTimes());
        exam.setCreatedAt(LocalDateTime.now());
        exam.setUpdatedAt(LocalDateTime.now());

        Exam savedExam = examRepository.save(exam);
        return convertToExamResponseDTO(savedExam);
    }

    @Override
    public ExamResponseDTO getExam(UUID id) {
        Exam exam = examRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Exam not found with id: " + id));
        return convertToExamResponseDTO(exam);
    }

    @Override
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

        // Update exam fields
        exam.setTitle(request.getTitle());
        exam.setDescription(request.getDescription());
        exam.setStartDate(request.getStartDate());
        exam.setEndDate(request.getEndDate());
        exam.setStatus(request.getStatus());
        exam.setCreator(creator);
        exam.setCourseCode(request.getCourseCode());
        exam.setTerm(term);
        exam.setAcademicYearGroup(academicYearGroup);
        exam.setSuccessPercentage(request.getSuccessPercentage());
        exam.setAllowedAttemptTimes(request.getAllowedAttemptTimes());
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
    public List<ExamResponseDTO> listExams() {
        List<Exam> exams = examRepository.findAll();
        return exams.stream()
                .map(this::convertToExamResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ExamResponseDTO> getExamsByAcademicYearIdAndTermOrder(UUID academicYearGroupId, UUID termId) {
        // Use the new repository method with @Query
        List<Exam> exams = examRepository.findExamsByAcademicYearAndTerm(academicYearGroupId, termId);
        
        return exams.stream()
                .map(this::convertToExamResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
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
        response.setCourseCode(exam.getCourseCode());
        response.setTermId(exam.getTerm().getId());
        response.setAcademicYearGroupId(exam.getAcademicYearGroup().getId());
        response.setSuccessPercentage(exam.getSuccessPercentage());
        response.setAllowedAttemptTimes(exam.getAllowedAttemptTimes());
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
    public List<SectionResponseDTO> listSections(UUID examId) {
        // Validate that exam exists
        examRepository.findById(examId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Exam not found with id: " + examId));

        List<ExamSection> sections = examSectionRepository.findByExamId(examId);
        return sections.stream()
                .map(this::convertToSectionResponseDTO)
                .collect(Collectors.toList());
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
        question.setCreatedAt(LocalDateTime.now());
        question.setUpdatedAt(LocalDateTime.now());

        ExamQuestion savedQuestion = examQuestionRepository.save(question);
        return convertToQuestionResponseDTO(savedQuestion);
    }

    @Override
    public List<QuestionResponseDTO> listQuestions(UUID examId) {
        // Validate that exam exists
        examRepository.findById(examId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Exam not found with id: " + examId));

        List<ExamQuestion> questions = examQuestionRepository.findByExamId(examId);
        return questions.stream()
                .map(this::convertToQuestionResponseDTO)
                .collect(Collectors.toList());
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
        question.setUpdatedAt(LocalDateTime.now());

        ExamQuestion updatedQuestion = examQuestionRepository.save(question);
        return convertToQuestionResponseDTO(updatedQuestion);
    }

    @Override
    public void deleteQuestion(UUID questionId) {
        ExamQuestion question = examQuestionRepository.findById(questionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Question not found with id: " + questionId));
        
        examQuestionRepository.delete(question);
    }

    @Override
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
    public List<ChoiceResponseDTO> listChoices(UUID questionId) {
        // Validate that question exists
        examQuestionRepository.findById(questionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Question not found with id: " + questionId));

        List<ExamQuestionChoice> choices = examQuestionChoiceRepository.findByExamQuestionId(questionId);
        return choices.stream()
                .map(this::convertToChoiceResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
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
        answerKey.setAcceptable(request.isAcceptable());
        answerKey.setSortOrder(request.getSortOrder());
        answerKey.setCreatedAt(LocalDateTime.now());
        answerKey.setUpdatedAt(LocalDateTime.now());

        ExamQuestionAnswerKey savedAnswerKey = examQuestionAnswerKeyRepository.save(answerKey);
        return convertToAnswerKeyResponseDTO(savedAnswerKey);
    }

    @Override
    public List<AnswerKeyResponseDTO> listAnswerKeys(UUID questionId) {
        // Validate that question exists
        examQuestionRepository.findById(questionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Question not found with id: " + questionId));

        List<ExamQuestionAnswerKey> answerKeys = examQuestionAnswerKeyRepository.findByExamQuestionId(questionId);
        return answerKeys.stream()
                .map(this::convertToAnswerKeyResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CodingTestCaseResponseDTO addTestCase(UUID questionId, CodingTestCaseRequestDTO request) {
        // Validate that question exists
        examQuestionRepository.findById(questionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Question not found with id: " + questionId));

        // Create new test case entity
        CodingTestCase testCase = new CodingTestCase();
        testCase.setId(UUID.randomUUID());
        testCase.setExamQuestion(examQuestionRepository.findById(questionId).get());
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
    public List<CodingTestCaseResponseDTO> listTestCases(UUID questionId) {
        // Validate that question exists
        examQuestionRepository.findById(questionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Question not found with id: " + questionId));

        List<CodingTestCase> testCases = codingTestCaseRepository.findByExamQuestionId(questionId);
        return testCases.stream()
                .map(this::convertToCodingTestCaseResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
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
    public StudentAttemptResponseDTO getStudentAttempt(UUID attemptId) {
        StudentExamAttempt attempt = studentExamAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student attempt not found with id: " + attemptId));
        return convertToStudentAttemptResponseDTO(attempt);
    }

    @Override
    public List<StudentAttemptResponseDTO> listStudentAttempts() {
        List<StudentExamAttempt> attempts = studentExamAttemptRepository.findAll();
        return attempts.stream()
                .map(this::convertToStudentAttemptResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<StudentAttemptResponseDTO> listStudentAttemptsByStudentIdAndExamId(UUID studentId, UUID examId) {
        // Validate that exam exists
        examRepository.findById(examId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Exam not found with id: " + examId));

        List<StudentExamAttempt> attempts = studentExamAttemptRepository.findByStudentIdAndExamId(studentId, examId);
        return attempts.stream()
                .map(this::convertToStudentAttemptResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<StudentAttemptResponseDTO> listStudentAttemptsByExamId(UUID examId) {
        // Validate that exam exists
        examRepository.findById(examId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Exam not found with id: " + examId));

        List<StudentExamAttempt> attempts = studentExamAttemptRepository.findByExamId(examId);
        return attempts.stream()
                .map(this::convertToStudentAttemptResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
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
        studentAnswer.setCreatedAt(LocalDateTime.now());
        studentAnswer.setUpdatedAt(LocalDateTime.now());

        StudentAnswerChoice savedAnswer = studentAnswerChoiceRepository.save(studentAnswer);
        return convertToStudentAnswerChoiceResponseDTO(savedAnswer);
    }

    @Override
    public StudentAnswerTextResponseDTO submitTextAnswer(UUID attemptId, StudentAnswerTextRequestDTO request) {
        // Validate that attempt exists
        StudentExamAttempt attempt = studentExamAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student attempt not found with id: " + attemptId));

        // Validate that question exists
        ExamQuestion question = examQuestionRepository.findById(request.getExamQuestionId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Question not found with id: " + request.getExamQuestionId()));

        // Create new student answer text entity
        StudentAnswerText studentAnswer = new StudentAnswerText();
        studentAnswer.setId(UUID.randomUUID());
        studentAnswer.setStudentExamAttempt(attempt);
        studentAnswer.setExamQuestion(question);
        studentAnswer.setQuestionPart(request.getQuestionPart());
        studentAnswer.setStudentAnswer(request.getStudentAnswer());
        studentAnswer.setSubmittedAt(LocalDateTime.now());
        studentAnswer.setCreatedAt(LocalDateTime.now());
        studentAnswer.setUpdatedAt(LocalDateTime.now());

        StudentAnswerText savedAnswer = studentAnswerTextRepository.save(studentAnswer);
        return convertToStudentAnswerTextResponseDTO(savedAnswer);
    }

    @Override
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
    public List<StudentCodingTestResultResponseDTO> listCodingTestResults(UUID codeAnswerId) {
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
        QuestionResponseDTO response = new QuestionResponseDTO();
        response.setId(question.getId());
        response.setExamId(question.getExam().getId());
        response.setSectionId(question.getSection() != null ? question.getSection().getId() : null);
        response.setQuestionPoolId(question.getQuestionPool() != null ? question.getQuestionPool().getId() : null);
        response.setQuestionText(question.getQuestionText());
        response.setQuestionType(question.getQuestionType());
        response.setExplanation(question.getExplanation());
        response.setProgrammingLanguageId(question.getProgrammingLanguage() != null ? question.getProgrammingLanguage().getId() : null);
        response.setTimeLimit(question.getTimeLimit());
        response.setMemoryLimit(question.getMemoryLimit());
        response.setMark(question.getMark());
        response.setPosition(question.getPosition());
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
        response.setAcceptable(answerKey.isAcceptable());
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
    private StudentAnswerTextResponseDTO convertToStudentAnswerTextResponseDTO(StudentAnswerText answer) {
        StudentAnswerTextResponseDTO response = new StudentAnswerTextResponseDTO();
        response.setId(answer.getId());
        response.setStudentAnswer(answer.getStudentAnswer());
        response.setQuestionPart(answer.getQuestionPart());
        response.setSubmittedAt(answer.getSubmittedAt());
        response.setIsCorrect(answer.getIsCorrect());
        response.setMarkObtained(answer.getMarkObtained());
        response.setSimilarityScore(answer.getSimilarityScore());
        return response;
    }

    // Helper method to convert StudentAnswerCode entity to StudentAnswerCodeResponseDTO
    private StudentAnswerCodeResponseDTO convertToStudentAnswerCodeResponseDTO(StudentAnswerCode answer) {
        StudentAnswerCodeResponseDTO response = new StudentAnswerCodeResponseDTO();
        response.setId(answer.getId());
        response.setSubmittedCode(answer.getSubmittedCode());
        response.setLanguageId(answer.getLanguage() != null ? answer.getLanguage().getId() : null);
        response.setTotalScore(answer.getTotalScore());
        response.setResultSummary(answer.getResultSummary());
        response.setAiScore(answer.getAiScore());
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
} 