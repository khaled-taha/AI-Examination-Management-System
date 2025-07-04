package com.university.exam.examManagement.services;

import com.university.exam.examManagement.dtos.request.*;
import com.university.exam.examManagement.dtos.response.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface ExamService {
    List<LanguagesResponseDTO> getAvailableLanguages();
    ExamResponseDTO saveExam(ExamRequestDTO request);
    ExamResponseDTO getExam(UUID id);
    void deleteExam(UUID id);
    List<ExamResponseDTO> getExams();

    List<ExamResponseDTO> getExamsByAcademicYearCourseId(UUID academicYearCourseId);
    Page<ExamResponseDTO> getExamsByAcademicYearCourseId(UUID academicYearCourseId, Pageable pageable);

    CanEnterExamResponseDTO canStudentEnterExam(UUID studentId, UUID examId);

    SectionResponseDTO saveSection(UUID examId, SectionRequestDTO request);
    List<SectionResponseDTO> getSections(UUID examId);
    PaginatedSectionsResponseDTO getSectionsPaginated(UUID examId, int page);

    ExamQuestionResponseDTO saveQuestion(UUID examId, QuestionRequestDTO request);

    void deleteQuestion(UUID questionId);

    ExamQuestionChoicesResponseDTO saveChoice(UUID questionId, ExamQuestionChoicesRequestDTO request);
    ExamQuestionChoicesResponseDTO getChoices(UUID questionId);

    ExamQuestionAnswerKeysResponseDTO saveAnswerKey(UUID questionId, ExamQuestionAnswerKeyRequestDTO request);
    ExamQuestionAnswerKeysResponseDTO getAnswerKeys(UUID questionId);

    ExamQuestionCodingTestCaseResponseDTO saveTestCase(UUID questionId, ExamQuestionCodingTestCaseRequestDTO request);
    ExamQuestionCodingTestCaseResponseDTO getTestCases(UUID questionId);

    StudentAttemptResponseDTO createStudentAttempt(StudentAttemptRequestDTO request);
    StudentAttemptResponseDTO getStudentAttempt(UUID attemptId);
    List<StudentAttemptResponseDTO> getStudentAttempts();
    List<StudentAttemptResponseDTO> getStudentAttemptsByStudentIdAndExamId(UUID studentId, UUID examId);
    List<StudentAttemptResponseDTO> getStudentAttemptsByExamId(UUID examId);
    List<StudentSectionViewDTO> getExamForStudent(UUID examId);
    PaginatedStudentSectionsResponseDTO getExamForStudentPaginated(UUID examId, int page);

    StudentAnswerChoicesResponseDTO submitChoiceAnswer(UUID attemptId, List<StudentAnswerChoiceRequestDTO> requests);
    StudentAnswerTextResponseDTO submitTextAnswers(UUID attemptId, List<StudentAnswerTextRequestDTO> requests);
    StudentAnswerCodeResponseDTO submitCodeAnswer(UUID attemptId, List<StudentAnswerCodeRequestDTO> requests);

    List<StudentCodingTestResultResponseDTO> getCodingTestResults(UUID codeAnswerId);

    void deleteChoice(UUID questionId, UUID choiceId);
    void deleteAnswerKey(UUID questionId, UUID answerKeyId);
    void deleteTestCase(UUID questionId, UUID testCaseId);
    CompletableFuture<Boolean> endExam(UUID attemptId);
    double getExamTotalPoints(UUID examId);

    StudentAnswerChoicesResponseDTO getStudentChoiceAnswer(UUID attemptId, UUID questionId);
    StudentAnswerTextResponseDTO getStudentTextAnswers(UUID attemptId, UUID questionId);
    StudentAnswerCodeResponseDTO getStudentCodeAnswer(UUID attemptId, UUID questionId);
    
    StudentExamResultResponseDTO getStudentExamResult(UUID attemptId);
} 