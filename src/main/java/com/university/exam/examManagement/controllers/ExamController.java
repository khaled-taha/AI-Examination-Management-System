package com.university.exam.examManagement.controllers;

import com.university.exam.examManagement.dtos.request.*;
import com.university.exam.examManagement.dtos.response.*;
import com.university.exam.examManagement.services.ExamService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/exams")
public class ExamController {
    private final ExamService examService;

    public ExamController(@Qualifier("DefaultExamServiceImpl") ExamService examService) {
        this.examService = examService;
    }

    @PostMapping
    public ResponseEntity<ExamResponseDTO> createExam(@Valid @RequestBody ExamRequestDTO request) {
        return ResponseEntity.ok(examService.createExam(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExamResponseDTO> getExam(@PathVariable UUID id) {
        return ResponseEntity.ok(examService.getExam(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExamResponseDTO> updateExam(@PathVariable UUID id, @Valid @RequestBody ExamRequestDTO request) {
        return ResponseEntity.ok(examService.updateExam(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExam(@PathVariable UUID id) {
        examService.deleteExam(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<ExamResponseDTO>> listExams() {
        return ResponseEntity.ok(examService.listExams());
    }

    @GetMapping("/academic-year/{academicYearGroupId}/term/{termId}")
    public ResponseEntity<List<ExamResponseDTO>> getExamsByAcademicYearIdAndTermOrder(
            @PathVariable UUID academicYearGroupId, 
            @PathVariable UUID termId) {
        return ResponseEntity.ok(examService.getExamsByAcademicYearIdAndTermOrder(academicYearGroupId, termId));
    }

    // Section APIs
    @PostMapping("/{examId}/sections")
    public ResponseEntity<SectionResponseDTO> createSection(@PathVariable UUID examId, @Valid @RequestBody SectionRequestDTO request) {
        return ResponseEntity.ok(examService.createSection(examId, request));
    }

    @GetMapping("/{examId}/sections")
    public ResponseEntity<List<SectionResponseDTO>> listSections(@PathVariable UUID examId) {
        return ResponseEntity.ok(examService.listSections(examId));
    }

    // Question APIs
    @PostMapping("/{examId}/questions")
    public ResponseEntity<QuestionResponseDTO> addQuestion(@PathVariable UUID examId, @Valid @RequestBody QuestionRequestDTO request) {
        return ResponseEntity.ok(examService.addQuestion(examId, request));
    }

    @PostMapping("/{examId}/questions/copy-from-pool")
    public ResponseEntity<QuestionResponseDTO> copyQuestionFromPool(@PathVariable UUID examId, @Valid @RequestBody CopyQuestionFromPoolRequestDTO request) {
        return ResponseEntity.ok(examService.copyQuestionFromPool(examId, request));
    }

    @GetMapping("/{examId}/questions")
    public ResponseEntity<List<QuestionResponseDTO>> listQuestions(@PathVariable UUID examId) {
        return ResponseEntity.ok(examService.listQuestions(examId));
    }

    @PutMapping("/questions/{questionId}")
    public ResponseEntity<QuestionResponseDTO> updateQuestion(@PathVariable UUID questionId, @Valid @RequestBody QuestionRequestDTO request) {
        return ResponseEntity.ok(examService.updateQuestion(questionId, request));
    }

    @DeleteMapping("/questions/{questionId}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable UUID questionId) {
        examService.deleteQuestion(questionId);
        return ResponseEntity.noContent().build();
    }

    // Choices APIs
    @PostMapping("/questions/{questionId}/choices")
    public ResponseEntity<ChoiceResponseDTO> addChoice(@PathVariable UUID questionId, @Valid @RequestBody ChoiceRequestDTO request) {
        return ResponseEntity.ok(examService.addChoice(questionId, request));
    }

    @GetMapping("/questions/{questionId}/choices")
    public ResponseEntity<List<ChoiceResponseDTO>> listChoices(@PathVariable UUID questionId) {
        return ResponseEntity.ok(examService.listChoices(questionId));
    }

    // Answer Key APIs
    @PostMapping("/questions/{questionId}/answer-keys")
    public ResponseEntity<AnswerKeyResponseDTO> addAnswerKey(@PathVariable UUID questionId, @Valid @RequestBody AnswerKeyRequestDTO request) {
        return ResponseEntity.ok(examService.addAnswerKey(questionId, request));
    }

    @GetMapping("/questions/{questionId}/answer-keys")
    public ResponseEntity<List<AnswerKeyResponseDTO>> listAnswerKeys(@PathVariable UUID questionId) {
        return ResponseEntity.ok(examService.listAnswerKeys(questionId));
    }

    // Coding Test Case APIs
    @PostMapping("/questions/{questionId}/test-cases")
    public ResponseEntity<CodingTestCaseResponseDTO> addTestCase(@PathVariable UUID questionId, @Valid @RequestBody CodingTestCaseRequestDTO request) {
        return ResponseEntity.ok(examService.addTestCase(questionId, request));
    }

    @GetMapping("/questions/{questionId}/test-cases")
    public ResponseEntity<List<CodingTestCaseResponseDTO>> listTestCases(@PathVariable UUID questionId) {
        return ResponseEntity.ok(examService.listTestCases(questionId));
    }

    // Student Attempt APIs
    @PostMapping("/attempts")
    public ResponseEntity<StudentAttemptResponseDTO> createStudentAttempt(@Valid @RequestBody StudentAttemptRequestDTO request) {
        return ResponseEntity.ok(examService.createStudentAttempt(request));
    }

    @GetMapping("/attempts/{attemptId}")
    public ResponseEntity<StudentAttemptResponseDTO> getStudentAttempt(@PathVariable UUID attemptId) {
        return ResponseEntity.ok(examService.getStudentAttempt(attemptId));
    }

    @GetMapping("/attempts")
    public ResponseEntity<List<StudentAttemptResponseDTO>> listStudentAttempts() {
        return ResponseEntity.ok(examService.listStudentAttempts());
    }

    @GetMapping("/attempts/student/{studentId}/exam/{examId}")
    public ResponseEntity<List<StudentAttemptResponseDTO>> listStudentAttemptsByStudentIdAndExamId(
            @PathVariable UUID studentId, 
            @PathVariable UUID examId) {
        return ResponseEntity.ok(examService.listStudentAttemptsByStudentIdAndExamId(studentId, examId));
    }

    @GetMapping("/{examId}/attempts")
    public ResponseEntity<List<StudentAttemptResponseDTO>> listStudentAttemptsByExamId(@PathVariable UUID examId) {
        return ResponseEntity.ok(examService.listStudentAttemptsByExamId(examId));
    }

    // Student Answers APIs
    @PostMapping("/attempts/{attemptId}/answers/choice")
    public ResponseEntity<StudentAnswerChoiceResponseDTO> submitChoiceAnswer(@PathVariable UUID attemptId, @Valid @RequestBody StudentAnswerChoiceRequestDTO request) {
        return ResponseEntity.ok(examService.submitChoiceAnswer(attemptId, request));
    }

    @PostMapping("/attempts/{attemptId}/answers/text")
    public ResponseEntity<StudentAnswerTextResponseDTO> submitTextAnswer(@PathVariable UUID attemptId, @Valid @RequestBody StudentAnswerTextRequestDTO request) {
        return ResponseEntity.ok(examService.submitTextAnswer(attemptId, request));
    }

    @PostMapping("/attempts/{attemptId}/answers/code")
    public ResponseEntity<StudentAnswerCodeResponseDTO> submitCodeAnswer(@PathVariable UUID attemptId, @Valid @RequestBody StudentAnswerCodeRequestDTO request) {
        return ResponseEntity.ok(examService.submitCodeAnswer(attemptId, request));
    }

    // Student Coding Test Result APIs
    @GetMapping("/answers/code/{codeAnswerId}/test-results")
    public ResponseEntity<List<StudentCodingTestResultResponseDTO>> listCodingTestResults(@PathVariable UUID codeAnswerId) {
        return ResponseEntity.ok(examService.listCodingTestResults(codeAnswerId));
    }

    @GetMapping("/{examId}/can-enter/{studentId}")
    public ResponseEntity<CanEnterExamResponseDTO> canStudentEnterExam(
            @PathVariable UUID examId, 
            @PathVariable UUID studentId) {
        return ResponseEntity.ok(examService.canStudentEnterExam(studentId, examId));
    }
}
