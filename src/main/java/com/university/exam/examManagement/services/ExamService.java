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
    List<ExamResponseDTO> listExams();

    SectionResponseDTO createSection(UUID examId, SectionRequestDTO request);
    List<SectionResponseDTO> listSections(UUID examId);

    QuestionResponseDTO addQuestion(UUID examId, QuestionRequestDTO request);
    List<QuestionResponseDTO> listQuestions(UUID examId);
    QuestionResponseDTO updateQuestion(UUID questionId, QuestionRequestDTO request);
    void deleteQuestion(UUID questionId);

    ChoiceResponseDTO addChoice(UUID questionId, ChoiceRequestDTO request);
    List<ChoiceResponseDTO> listChoices(UUID questionId);

    AnswerKeyResponseDTO addAnswerKey(UUID questionId, AnswerKeyRequestDTO request);
    List<AnswerKeyResponseDTO> listAnswerKeys(UUID questionId);

    CodingTestCaseResponseDTO addTestCase(UUID questionId, CodingTestCaseRequestDTO request);
    List<CodingTestCaseResponseDTO> listTestCases(UUID questionId);

    StudentAttemptResponseDTO createStudentAttempt(StudentAttemptRequestDTO request);
    StudentAttemptResponseDTO getStudentAttempt(UUID attemptId);
    List<StudentAttemptResponseDTO> listStudentAttempts();

    StudentAnswerChoiceResponseDTO submitChoiceAnswer(UUID attemptId, StudentAnswerChoiceRequestDTO request);
    StudentAnswerTextResponseDTO submitTextAnswer(UUID attemptId, StudentAnswerTextRequestDTO request);
    StudentAnswerCodeResponseDTO submitCodeAnswer(UUID attemptId, StudentAnswerCodeRequestDTO request);

    List<StudentCodingTestResultResponseDTO> listCodingTestResults(UUID codeAnswerId);
} 