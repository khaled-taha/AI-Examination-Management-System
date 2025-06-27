package com.university.exam.examManagement.controllers;

import com.university.exam.examManagement.dtos.request.StudentAnswerChoiceRequestDTO;
import com.university.exam.examManagement.dtos.request.StudentAnswerCodeRequestDTO;
import com.university.exam.examManagement.dtos.request.StudentAnswerTextRequestDTO;
import com.university.exam.examManagement.dtos.request.StudentAttemptRequestDTO;
import com.university.exam.examManagement.dtos.response.*;
import com.university.exam.examManagement.services.ExamService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/student/exams")
public class StudentExamController {

    private final ExamService examService;

    public StudentExamController(@Qualifier("DefaultExamServiceImpl") ExamService examService) {
        this.examService = examService;
    }

    @GetMapping("/{examId}/can-enter/{studentId}")
    public ResponseEntity<CanEnterExamResponseDTO> canStudentEnterExam(
            @PathVariable UUID examId,
            @PathVariable UUID studentId) {
        return ResponseEntity.ok(examService.canStudentEnterExam(studentId, examId));
    }

    // Student Attempt APIs
    @PostMapping("/attempts")
    public ResponseEntity<StudentAttemptResponseDTO> createStudentAttempt(@Valid @RequestBody StudentAttemptRequestDTO request) {
        return ResponseEntity.ok(examService.createStudentAttempt(request));
    }

    // End Exam Attempt
    @PostMapping("/attempts/{attemptId}/end")
    public ResponseEntity<StudentAttemptResponseDTO> endExam(@PathVariable UUID attemptId) {
        return ResponseEntity.ok(examService.endExam(attemptId));
    }

    @GetMapping("/attempts/{attemptId}")
    public ResponseEntity<StudentAttemptResponseDTO> getStudentAttempt(@PathVariable UUID attemptId) {
        return ResponseEntity.ok(examService.getStudentAttempt(attemptId));
    }

    @GetMapping("/attempts/student/{studentId}/exam/{examId}")
    public ResponseEntity<List<StudentAttemptResponseDTO>> getStudentAttemptsByStudentIdAndExamId(
            @PathVariable UUID studentId,
            @PathVariable UUID examId) {
        return ResponseEntity.ok(examService.getStudentAttemptsByStudentIdAndExamId(studentId, examId));
    }

    @GetMapping("/{examId}/attempts")
    @Deprecated
    public ResponseEntity<List<StudentAttemptResponseDTO>> getStudentsAttemptsByExamId(@PathVariable UUID examId) {
        return ResponseEntity.ok(examService.getStudentAttemptsByExamId(examId));
    }

    @GetMapping("/{examId}/student-view")
    @Deprecated
    public ResponseEntity<List<StudentSectionViewDTO>> renderExamForStudent(@PathVariable UUID examId) {
        return ResponseEntity.ok(examService.getExamForStudent(examId));
    }

    @GetMapping("/{examId}/student-view/paginated")
    public ResponseEntity<PaginatedStudentSectionsResponseDTO> renderExamForStudentPaginated(
            @PathVariable UUID examId,
            @RequestParam(defaultValue = "1") int page) {
        if (page < 1) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(examService.getExamForStudentPaginated(examId, page));
    }

    // Student Answers APIs
    @PostMapping("/attempts/{attemptId}/answers/choice")
    public ResponseEntity<StudentAnswerChoicesResponseDTO> submitChoiceAnswer(@PathVariable UUID attemptId, @Valid @RequestBody List<StudentAnswerChoiceRequestDTO> requests) {
        return ResponseEntity.ok(examService.submitChoiceAnswer(attemptId, requests));
    }

    @PostMapping("/attempts/{attemptId}/answers/text")
    public ResponseEntity<StudentAnswerTextResponseDTO> submitTextAnswer(@PathVariable UUID attemptId, @Valid @RequestBody List<StudentAnswerTextRequestDTO> requests) {
        return ResponseEntity.ok(examService.submitTextAnswers(attemptId, requests));
    }

    @PostMapping("/attempts/{attemptId}/answers/code")
    public ResponseEntity<StudentAnswerCodeResponseDTO> submitCodeAnswer(@PathVariable UUID attemptId, @Valid @RequestBody List<StudentAnswerCodeRequestDTO> requests) {
        return ResponseEntity.ok(examService.submitCodeAnswer(attemptId, requests));
    }

    @GetMapping("/attempts")
    @Deprecated
    public ResponseEntity<List<StudentAttemptResponseDTO>> getStudentAttempts() {
        return ResponseEntity.ok(examService.getStudentAttempts());
    }

}
