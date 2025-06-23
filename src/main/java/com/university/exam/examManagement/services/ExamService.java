package com.university.exam.examManagement.services;

import com.university.exam.examManagement.dtos.request.*;
import com.university.exam.examManagement.dtos.response.*;
import java.util.List;
import java.util.UUID;

public interface ExamService {
    ExamResponseDTO createExam(ExamRequestDTO request);
    ExamResponseDTO getExam(UUID id);
    ExamResponseDTO updateExam(UUID id, ExamRequestDTO request);
    void deleteExam(UUID id);
    List<ExamResponseDTO> getExams();

    List<ExamResponseDTO> getExamsByAcademicYearCourseId(UUID academicYearCourseId);

    CanEnterExamResponseDTO canStudentEnterExam(UUID studentId, UUID examId);

    SectionResponseDTO createSection(UUID examId, SectionRequestDTO request);
    List<SectionResponseDTO> getSections(UUID examId);
    PaginatedSectionsResponseDTO getSectionsPaginated(UUID examId, int page);

    QuestionResponseDTO addQuestion(UUID examId, QuestionRequestDTO request);

    // List<QuestionResponseDTO> getQuestions(UUID examId);

    QuestionResponseDTO updateQuestion(UUID questionId, QuestionRequestDTO request);
    void deleteQuestion(UUID questionId);

    ChoiceResponseDTO saveChoice(UUID questionId, ChoiceRequestDTO request);
    List<ChoiceResponseDTO> getChoices(UUID questionId);

    AnswerKeyResponseDTO saveAnswerKey(UUID questionId, AnswerKeyRequestDTO request);
    List<AnswerKeyResponseDTO> getAnswerKeys(UUID questionId);

    CodingTestCaseResponseDTO saveTestCase(UUID questionId, CodingTestCaseRequestDTO request);
    List<CodingTestCaseResponseDTO> getTestCases(UUID questionId);

    StudentAttemptResponseDTO createStudentAttempt(StudentAttemptRequestDTO request);
    StudentAttemptResponseDTO getStudentAttempt(UUID attemptId);
    List<StudentAttemptResponseDTO> getStudentAttempts();
    List<StudentAttemptResponseDTO> getStudentAttemptsByStudentIdAndExamId(UUID studentId, UUID examId);
    List<StudentAttemptResponseDTO> getStudentAttemptsByExamId(UUID examId);
    List<StudentSectionViewDTO> getExamForStudent(UUID examId);
    PaginatedStudentSectionsResponseDTO getExamForStudentPaginated(UUID examId, int page);

    StudentAnswerChoiceResponseDTO submitChoiceAnswer(UUID attemptId, StudentAnswerChoiceRequestDTO request);
    StudentAnswerTextResponseDTO submitTextAnswers(UUID attemptId, StudentAnswerTextRequestDTO request);
    StudentAnswerCodeResponseDTO submitCodeAnswer(UUID attemptId, StudentAnswerCodeRequestDTO request);

    List<StudentCodingTestResultResponseDTO> getCodingTestResults(UUID codeAnswerId);

    void deleteChoice(UUID questionId, UUID choiceId);
    void deleteAnswerKey(UUID questionId, UUID answerKeyId);
    void deleteTestCase(UUID questionId, UUID testCaseId);
    StudentAttemptResponseDTO endExam(UUID attemptId);
    double getExamTotalPoints(UUID examId);
} 