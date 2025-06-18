package com.university.exam.examManagement.services;

import com.university.exam.examManagement.dtos.request.*;
import com.university.exam.examManagement.dtos.response.*;
import com.university.exam.examManagement.repos.*;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

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

    @Override
    public ExamResponseDTO createExam(ExamRequestDTO request) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public ExamResponseDTO getExam(UUID id) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public ExamResponseDTO updateExam(UUID id, ExamRequestDTO request) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void deleteExam(UUID id) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public List<ExamResponseDTO> listExams() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public SectionResponseDTO createSection(UUID examId, SectionRequestDTO request) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public List<SectionResponseDTO> listSections(UUID examId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public QuestionResponseDTO addQuestion(UUID examId, QuestionRequestDTO request) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public List<QuestionResponseDTO> listQuestions(UUID examId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public QuestionResponseDTO updateQuestion(UUID questionId, QuestionRequestDTO request) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void deleteQuestion(UUID questionId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public ChoiceResponseDTO addChoice(UUID questionId, ChoiceRequestDTO request) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public List<ChoiceResponseDTO> listChoices(UUID questionId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public AnswerKeyResponseDTO addAnswerKey(UUID questionId, AnswerKeyRequestDTO request) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public List<AnswerKeyResponseDTO> listAnswerKeys(UUID questionId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public CodingTestCaseResponseDTO addTestCase(UUID questionId, CodingTestCaseRequestDTO request) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public List<CodingTestCaseResponseDTO> listTestCases(UUID questionId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public StudentAttemptResponseDTO createStudentAttempt(StudentAttemptRequestDTO request) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public StudentAttemptResponseDTO getStudentAttempt(UUID attemptId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public List<StudentAttemptResponseDTO> listStudentAttempts() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public StudentAnswerChoiceResponseDTO submitChoiceAnswer(UUID attemptId, StudentAnswerChoiceRequestDTO request) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public StudentAnswerTextResponseDTO submitTextAnswer(UUID attemptId, StudentAnswerTextRequestDTO request) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public StudentAnswerCodeResponseDTO submitCodeAnswer(UUID attemptId, StudentAnswerCodeRequestDTO request) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public List<StudentCodingTestResultResponseDTO> listCodingTestResults(UUID codeAnswerId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
} 