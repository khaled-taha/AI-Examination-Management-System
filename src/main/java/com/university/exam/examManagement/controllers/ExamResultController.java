package com.university.exam.examManagement.controllers;


import com.university.exam.examManagement.dtos.response.*;
import com.university.exam.examManagement.services.ExamService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/exams/result")
public class ExamResultController {

    private final ExamService examService;

    public ExamResultController(@Qualifier("DefaultExamServiceImpl") ExamService examService) {
        this.examService = examService;
    }

    // Get Exam Total Points
    @GetMapping("/{examId}/total-points")
    public ResponseEntity<Double> getExamTotalPoints(@PathVariable UUID examId) {
        return ResponseEntity.ok(examService.getExamTotalPoints(examId));
    }

    // Student Answers Retrieval APIs
    @GetMapping("/attempts/{attemptId}/questions/{questionId}/choice-answer")
    public ResponseEntity<StudentAnswerChoicesResponseDTO> getStudentChoiceAnswer(
            @PathVariable UUID attemptId,
            @PathVariable UUID questionId) {
        return ResponseEntity.ok(examService.getStudentChoiceAnswer(attemptId, questionId));
    }

    @GetMapping("/attempts/{attemptId}/questions/{questionId}/text-answers")
    public ResponseEntity<StudentAnswerTextResponseDTO> getStudentTextAnswers(
            @PathVariable UUID attemptId,
            @PathVariable UUID questionId) {
        return ResponseEntity.ok(examService.getStudentTextAnswers(attemptId, questionId));
    }

    @GetMapping("/attempts/{attemptId}/questions/{questionId}/code-answer")
    public ResponseEntity<StudentAnswerCodeResponseDTO> getStudentCodeAnswer(
            @PathVariable UUID attemptId,
            @PathVariable UUID questionId) {
        return ResponseEntity.ok(examService.getStudentCodeAnswer(attemptId, questionId));
    }

    // Student Exam Result API
    @GetMapping("/attempts/{attemptId}/result")
    public ResponseEntity<StudentExamResultResponseDTO> getStudentExamResult(@PathVariable UUID attemptId) {
        return ResponseEntity.ok(examService.getStudentExamResult(attemptId));
    }

    @GetMapping("/answers/code/{codeAnswerId}/test-results")
    @Deprecated
    public ResponseEntity<List<StudentCodingTestResultResponseDTO>> getCodingTestResults(@PathVariable UUID codeAnswerId) {
        return ResponseEntity.ok(examService.getCodingTestResults(codeAnswerId));
    }
}
