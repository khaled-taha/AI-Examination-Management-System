package com.university.exam.examManagement.dtos.response;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class StudentAnswerKeyQuestionViewDTO extends StudentQuestionViewDTO {
    // For student view, we don't expose answer keys
    // Students just see the question text and need to provide their answers
    private List<MatchingItem> matchingItems;

    @Data
    @NoArgsConstructor
    public static class MatchingItem {
        private UUID itemId;
        private String questionPart;
        private int sortOrder;
        public MatchingItem(UUID itemId, String questionPart, int sortOrder) {
            this.itemId = itemId;
            this.questionPart = questionPart;
            this.sortOrder = sortOrder;
        }
    }
} 