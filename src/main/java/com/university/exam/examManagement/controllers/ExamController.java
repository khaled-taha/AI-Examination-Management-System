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
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'VIEWER', 'STUDENT')")
    public ResponseEntity<List<LanguagesResponseDTO>> getAvailableLanguages() {
        return ResponseEntity.ok(examService.getAvailableLanguages());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    public ResponseEntity<ExamResponseDTO> saveExam(@Valid @RequestBody ExamRequestDTO request) {
        return ResponseEntity.ok(examService.saveExam(request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'VIEWER', 'STUDENT')")
    public ResponseEntity<ExamResponseDTO> getExam(@PathVariable UUID id) {
        return ResponseEntity.ok(examService.getExam(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    public ResponseEntity<Void> deleteExam(@PathVariable UUID id) {
        examService.deleteExam(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/academic-year/{academicYearCourseId}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'VIEWER', 'STUDENT')")
    public ResponseEntity<List<ExamResponseDTO>> getExamsByAcademicYearCourse(
            @PathVariable UUID academicYearCourseId) {
        return ResponseEntity.ok(examService.getExamsByAcademicYearCourseId(academicYearCourseId));
    }


    // Section APIs
    @PostMapping("/{examId}/sections")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    public ResponseEntity<SectionResponseDTO> saveSection(@PathVariable UUID examId, @Valid @RequestBody SectionRequestDTO request) {
        return ResponseEntity.ok(examService.saveSection(examId, request));
    }

    @GetMapping("/{examId}/sections")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'VIEWER')")
    public ResponseEntity<List<SectionResponseDTO>> getSections(@PathVariable UUID examId) {
        return ResponseEntity.ok(examService.getSections(examId));
    }

    @GetMapping("/{examId}/sections/paginated")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'VIEWER')")
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
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    public ResponseEntity<ExamQuestionResponseDTO> saveQuestion(@PathVariable UUID examId, @Valid @RequestBody QuestionRequestDTO request) {
        return ResponseEntity.ok(examService.saveQuestion(examId, request));
    }

    @DeleteMapping("/questions/{questionId}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    public ResponseEntity<Void> deleteQuestion(@PathVariable UUID questionId) {
        examService.deleteQuestion(questionId);
        return ResponseEntity.noContent().build();
    }

    // Choices APIs
    @PostMapping("/questions/{questionId}/choices")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    public ResponseEntity<ExamQuestionChoicesResponseDTO> saveChoices(@PathVariable UUID questionId, @Valid @RequestBody ExamQuestionChoicesRequestDTO request) {
        return ResponseEntity.ok(examService.saveChoice(questionId, request));
    }

    @GetMapping("/questions/{questionId}/choices")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'VIEWER')")
    public ResponseEntity<ExamQuestionChoicesResponseDTO> getChoices(@PathVariable UUID questionId) {
        return ResponseEntity.ok(examService.getChoices(questionId));
    }

    // Delete Choice
    @DeleteMapping("/questions/{questionId}/choices/{choiceId}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    public ResponseEntity<Void> deleteChoice(@PathVariable UUID questionId, @PathVariable UUID choiceId) {
        examService.deleteChoice(questionId, choiceId);
        return ResponseEntity.noContent().build();
    }

    // Answer Key APIs
    @PostMapping("/questions/{questionId}/answer-keys")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    public ResponseEntity<ExamQuestionAnswerKeysResponseDTO> saveAnswerKeys(@PathVariable UUID questionId, @Valid @RequestBody ExamQuestionAnswerKeyRequestDTO request) {
        return ResponseEntity.ok(examService.saveAnswerKey(questionId, request));
    }

    @GetMapping("/questions/{questionId}/answer-keys")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'VIEWER')")
    public ResponseEntity<ExamQuestionAnswerKeysResponseDTO> getAnswerKeys(@PathVariable UUID questionId) {
        return ResponseEntity.ok(examService.getAnswerKeys(questionId));
    }

    // Delete Answer Key
    @DeleteMapping("/questions/{questionId}/answer-keys/{answerKeyId}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    public ResponseEntity<Void> deleteAnswerKey(@PathVariable UUID questionId, @PathVariable UUID answerKeyId) {
        examService.deleteAnswerKey(questionId, answerKeyId);
        return ResponseEntity.noContent().build();
    }

    // Coding Test Case APIs
    @PostMapping("/questions/{questionId}/test-cases")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    public ResponseEntity<ExamQuestionCodingTestCaseResponseDTO> saveTestCases(@PathVariable UUID questionId, @Valid @RequestBody ExamQuestionCodingTestCaseRequestDTO request) {
        return ResponseEntity.ok(examService.saveTestCase(questionId, request));
    }

    @GetMapping("/questions/{questionId}/test-cases")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'VIEWER')")
    public ResponseEntity<ExamQuestionCodingTestCaseResponseDTO> getTestCases(@PathVariable UUID questionId) {
        return ResponseEntity.ok(examService.getTestCases(questionId));
    }

    // Delete Test Case
    @DeleteMapping("/questions/{questionId}/test-cases/{testCaseId}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    public ResponseEntity<Void> deleteTestCase(@PathVariable UUID questionId, @PathVariable UUID testCaseId) {
        examService.deleteTestCase(questionId, testCaseId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Deprecated
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'VIEWER', 'STUDENT')")
    public ResponseEntity<List<ExamResponseDTO>> getExams() {
        return ResponseEntity.ok(examService.getExams());
    }

    @GetMapping("/academic-year/{academicYearCourseId}/pages")
    @Deprecated
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'VIEWER', 'STUDENT')")
    public ResponseEntity<Page<ExamResponseDTO>> getExamsByAcademicYearCourse(
            @PathVariable UUID academicYearCourseId,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(examService.getExamsByAcademicYearCourseId(academicYearCourseId, pageable));
    }
}
