package com.university.exam.examManagement.controllers;

import com.university.exam.examManagement.dtos.request.*;
import com.university.exam.examManagement.dtos.response.*;
import com.university.exam.examManagement.entities.ProgrammingLanguage;
import com.university.exam.examManagement.services.ExamService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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

    @GetMapping("/languages")
    public ResponseEntity<List<LanguagesResponseDTO>> getAvailableLanguages() {
        return ResponseEntity.ok(examService.getAvailableLanguages());
    }

    @PostMapping
    public ResponseEntity<ExamResponseDTO> saveExam(@Valid @RequestBody ExamRequestDTO request) {
        return ResponseEntity.ok(examService.saveExam(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExamResponseDTO> getExam(@PathVariable UUID id) {
        return ResponseEntity.ok(examService.getExam(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExam(@PathVariable UUID id) {
        examService.deleteExam(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/academic-year/{academicYearCourseId}")
    public ResponseEntity<List<ExamResponseDTO>> getExamsByAcademicYearCourse(
            @PathVariable UUID academicYearCourseId) {
        return ResponseEntity.ok(examService.getExamsByAcademicYearCourseId(academicYearCourseId));
    }


    // Section APIs
    @PostMapping("/{examId}/sections")
    public ResponseEntity<SectionResponseDTO> saveSection(@PathVariable UUID examId, @Valid @RequestBody SectionRequestDTO request) {
        return ResponseEntity.ok(examService.saveSection(examId, request));
    }

    @GetMapping("/{examId}/sections")
    public ResponseEntity<List<SectionResponseDTO>> getSections(@PathVariable UUID examId) {
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
    public ResponseEntity<ExamQuestionResponseDTO> saveQuestion(@PathVariable UUID examId, @Valid @RequestBody QuestionRequestDTO request) {
        return ResponseEntity.ok(examService.saveQuestion(examId, request));
    }

    @DeleteMapping("/questions/{questionId}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable UUID questionId) {
        examService.deleteQuestion(questionId);
        return ResponseEntity.noContent().build();
    }

    // Choices APIs
    @PostMapping("/questions/{questionId}/choices")
    public ResponseEntity<ExamQuestionChoicesResponseDTO> saveChoices(@PathVariable UUID questionId, @Valid @RequestBody ExamQuestionChoicesRequestDTO request) {
        return ResponseEntity.ok(examService.saveChoice(questionId, request));
    }

    @GetMapping("/questions/{questionId}/choices")
    public ResponseEntity<ExamQuestionChoicesResponseDTO> getChoices(@PathVariable UUID questionId) {
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
    public ResponseEntity<ExamQuestionAnswerKeysResponseDTO> saveAnswerKeys(@PathVariable UUID questionId, @Valid @RequestBody ExamQuestionAnswerKeyRequestDTO request) {
        return ResponseEntity.ok(examService.saveAnswerKey(questionId, request));
    }

    @GetMapping("/questions/{questionId}/answer-keys")
    public ResponseEntity<ExamQuestionAnswerKeysResponseDTO> getAnswerKeys(@PathVariable UUID questionId) {
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
    public ResponseEntity<ExamQuestionCodingTestCaseResponseDTO> saveTestCases(@PathVariable UUID questionId, @Valid @RequestBody ExamQuestionCodingTestCaseRequestDTO request) {
        return ResponseEntity.ok(examService.saveTestCase(questionId, request));
    }

    @GetMapping("/questions/{questionId}/test-cases")
    public ResponseEntity<ExamQuestionCodingTestCaseResponseDTO> getTestCases(@PathVariable UUID questionId) {
        return ResponseEntity.ok(examService.getTestCases(questionId));
    }

    // Delete Test Case
    @DeleteMapping("/questions/{questionId}/test-cases/{testCaseId}")
    public ResponseEntity<Void> deleteTestCase(@PathVariable UUID questionId, @PathVariable UUID testCaseId) {
        examService.deleteTestCase(questionId, testCaseId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Deprecated
    public ResponseEntity<List<ExamResponseDTO>> getExams() {
        return ResponseEntity.ok(examService.getExams());
    }

    @GetMapping("/academic-year/{academicYearCourseId}/pages")
    @Deprecated
    public ResponseEntity<Page<ExamResponseDTO>> getExamsByAcademicYearCourse(
            @PathVariable UUID academicYearCourseId,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(examService.getExamsByAcademicYearCourseId(academicYearCourseId, pageable));
    }
}
