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
        return ResponseEntity.ok(examService.getExams());
    }

    @GetMapping("/academic-year/{academicYearCourseId}")
    public ResponseEntity<List<ExamResponseDTO>> getExamsByAcademicYearCourse(
            @PathVariable UUID academicYearCourseId) {
        return ResponseEntity.ok(examService.getExamsByAcademicYearCourseId(academicYearCourseId));
    }

    // Section APIs
    @PostMapping("/{examId}/sections")
    public ResponseEntity<SectionResponseDTO> createSection(@PathVariable UUID examId, @Valid @RequestBody SectionRequestDTO request) {
        return ResponseEntity.ok(examService.createSection(examId, request));
    }

    @GetMapping("/{examId}/sections")
    public ResponseEntity<List<SectionResponseDTO>> listSections(@PathVariable UUID examId) {
        return ResponseEntity.ok(examService.getSections(examId));
    }

    @GetMapping("/{examId}/sections/paginated")
    public ResponseEntity<PaginatedSectionsResponseDTO> getSectionsPaginated(
            @PathVariable UUID examId, 
            @RequestParam(defaultValue = "1") int page) {
        if (page < 1) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(examService.getSectionsPaginated(examId, page));
    }

    // Question APIs
    @PostMapping("/{examId}/questions")
    public ResponseEntity<QuestionResponseDTO> addQuestion(@PathVariable UUID examId, @Valid @RequestBody QuestionRequestDTO request) {
        return ResponseEntity.ok(examService.addQuestion(examId, request));
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
    public ResponseEntity<ChoiceResponseDTO> saveChoice(@PathVariable UUID questionId, @Valid @RequestBody ChoiceRequestDTO request) {
        return ResponseEntity.ok(examService.saveChoice(questionId, request));
    }

    @GetMapping("/questions/{questionId}/choices")
    public ResponseEntity<List<ChoiceResponseDTO>> listChoices(@PathVariable UUID questionId) {
        return ResponseEntity.ok(examService.getChoices(questionId));
    }

    // Delete Choice
    @DeleteMapping("/questions/{questionId}/choices/{choiceId}")
    public ResponseEntity<Void> deleteChoice(@PathVariable UUID questionId, @PathVariable UUID choiceId) {
        examService.deleteChoice(questionId, choiceId);
        return ResponseEntity.noContent().build();
    }

    // Answer Key APIs
    @PostMapping("/questions/{questionId}/answer-keys")
    public ResponseEntity<AnswerKeyResponseDTO> saveAnswerKey(@PathVariable UUID questionId, @Valid @RequestBody AnswerKeyRequestDTO request) {
        return ResponseEntity.ok(examService.saveAnswerKey(questionId, request));
    }

    @GetMapping("/questions/{questionId}/answer-keys")
    public ResponseEntity<List<AnswerKeyResponseDTO>> listAnswerKeys(@PathVariable UUID questionId) {
        return ResponseEntity.ok(examService.getAnswerKeys(questionId));
    }

    // Delete Answer Key
    @DeleteMapping("/questions/{questionId}/answer-keys/{answerKeyId}")
    public ResponseEntity<Void> deleteAnswerKey(@PathVariable UUID questionId, @PathVariable UUID answerKeyId) {
        examService.deleteAnswerKey(questionId, answerKeyId);
        return ResponseEntity.noContent().build();
    }

    // Coding Test Case APIs
    @PostMapping("/questions/{questionId}/test-cases")
    public ResponseEntity<CodingTestCaseResponseDTO> saveTestCase(@PathVariable UUID questionId, @Valid @RequestBody CodingTestCaseRequestDTO request) {
        return ResponseEntity.ok(examService.saveTestCase(questionId, request));
    }

    @GetMapping("/questions/{questionId}/test-cases")
    public ResponseEntity<List<CodingTestCaseResponseDTO>> listTestCases(@PathVariable UUID questionId) {
        return ResponseEntity.ok(examService.getTestCases(questionId));
    }

    // Delete Test Case
    @DeleteMapping("/questions/{questionId}/test-cases/{testCaseId}")
    public ResponseEntity<Void> deleteTestCase(@PathVariable UUID questionId, @PathVariable UUID testCaseId) {
        examService.deleteTestCase(questionId, testCaseId);
        return ResponseEntity.noContent().build();
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
        return ResponseEntity.ok(examService.getStudentAttempts());
    }

    @GetMapping("/attempts/student/{studentId}/exam/{examId}")
    public ResponseEntity<List<StudentAttemptResponseDTO>> listStudentAttemptsByStudentIdAndExamId(
            @PathVariable UUID studentId, 
            @PathVariable UUID examId) {
        return ResponseEntity.ok(examService.getStudentAttemptsByStudentIdAndExamId(studentId, examId));
    }

    @GetMapping("/{examId}/attempts")
    public ResponseEntity<List<StudentAttemptResponseDTO>> listStudentAttemptsByExamId(@PathVariable UUID examId) {
        return ResponseEntity.ok(examService.getStudentAttemptsByExamId(examId));
    }

    @GetMapping("/{examId}/student-view")
    public ResponseEntity<List<StudentSectionViewDTO>> getExamForStudent(@PathVariable UUID examId) {
        return ResponseEntity.ok(examService.getExamForStudent(examId));
    }

    @GetMapping("/{examId}/student-view/paginated")
    public ResponseEntity<PaginatedStudentSectionsResponseDTO> getExamForStudentPaginated(
            @PathVariable UUID examId, 
            @RequestParam(defaultValue = "1") int page) {
        if (page < 1) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(examService.getExamForStudentPaginated(examId, page));
    }

    // Student Answers APIs
    @PostMapping("/attempts/{attemptId}/answers/choice")
    public ResponseEntity<StudentAnswerChoiceResponseDTO> submitChoiceAnswer(@PathVariable UUID attemptId, @Valid @RequestBody StudentAnswerChoiceRequestDTO request) {
        return ResponseEntity.ok(examService.submitChoiceAnswer(attemptId, request));
    }

    @PostMapping("/attempts/{attemptId}/answers/text")
    public ResponseEntity<StudentAnswerTextResponseDTO> submitTextAnswer(@PathVariable UUID attemptId, @Valid @RequestBody StudentAnswerTextRequestDTO request) {
        return ResponseEntity.ok(examService.submitTextAnswers(attemptId, request));
    }

    @PostMapping("/attempts/{attemptId}/answers/code")
    public ResponseEntity<StudentAnswerCodeResponseDTO> submitCodeAnswer(@PathVariable UUID attemptId, @Valid @RequestBody StudentAnswerCodeRequestDTO request) {
        return ResponseEntity.ok(examService.submitCodeAnswer(attemptId, request));
    }

    // Student Coding Test Result APIs
    @GetMapping("/answers/code/{codeAnswerId}/test-results")
    public ResponseEntity<List<StudentCodingTestResultResponseDTO>> listCodingTestResults(@PathVariable UUID codeAnswerId) {
        return ResponseEntity.ok(examService.getCodingTestResults(codeAnswerId));
    }

    @GetMapping("/{examId}/can-enter/{studentId}")
    public ResponseEntity<CanEnterExamResponseDTO> canStudentEnterExam(
            @PathVariable UUID examId, 
            @PathVariable UUID studentId) {
        return ResponseEntity.ok(examService.canStudentEnterExam(studentId, examId));
    }

    // End Exam Attempt
    @PostMapping("/attempts/{attemptId}/end")
    public ResponseEntity<StudentAttemptResponseDTO> endExam(@PathVariable UUID attemptId) {
        return ResponseEntity.ok(examService.endExam(attemptId));
    }

    // Get Exam Total Points
    @GetMapping("/{examId}/total-points")
    public ResponseEntity<Double> getExamTotalPoints(@PathVariable UUID examId) {
        return ResponseEntity.ok(examService.getExamTotalPoints(examId));
    }
}
