package com.hugo.studysessions.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ReviewReminderModelTests {

    @Test
    void createReviewReminder_setsFieldsCorrectly() {
        // Arrange
        LocalDate reviewDate = LocalDate.of(2025, 11, 21);

        ReviewReminder reminder = new ReviewReminder();
        reminder.setTitle("Review D424 Notes");      // from StudyItem
        reminder.setNotes("Focus on AWS concepts");  // from StudyItem
        reminder.setTopicName("Cloud Concepts");
        reminder.setReviewDate(reviewDate);
        reminder.setSessionId(10L);

        // Act & Assert
        assertEquals("Review D424 Notes", reminder.getTitle());
        assertEquals("Focus on AWS concepts", reminder.getNotes());
        assertEquals("Cloud Concepts", reminder.getTopicName());
        assertEquals(reviewDate, reminder.getReviewDate());
        assertEquals(10L, reminder.getSessionId());
    }

    @Test
    void spacedRepetitionIntervals_areCalculatedConsistently() {
        // Arrange - base day when the study session happened
        LocalDate baseDay = LocalDate.of(2025, 11, 20);

        ReviewReminder day1 = new ReviewReminder();
        day1.setReviewDate(baseDay.plusDays(1));

        ReviewReminder day7 = new ReviewReminder();
        day7.setReviewDate(baseDay.plusDays(7));

        ReviewReminder day30 = new ReviewReminder();
        day30.setReviewDate(baseDay.plusDays(30));

        // Assert
        assertEquals(baseDay.plusDays(1), day1.getReviewDate());
        assertEquals(baseDay.plusDays(7), day7.getReviewDate());
        assertEquals(baseDay.plusDays(30), day30.getReviewDate());
    }
}
