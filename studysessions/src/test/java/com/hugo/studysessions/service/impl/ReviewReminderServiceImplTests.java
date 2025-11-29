package com.hugo.studysessions.service.impl;

import com.hugo.studysessions.model.ReviewReminder;
import com.hugo.studysessions.model.StudySession;
import com.hugo.studysessions.repository.ReviewReminderRepository;
import com.hugo.studysessions.repository.StudySessionRepository;
import com.hugo.studysessions.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ReviewReminderServiceImplTests {

    @Test
    void generateRemindersForSession_createsFiveSpacedReminders() {
        // Mock dependencies
        ReviewReminderRepository reminderRepository = mock(ReviewReminderRepository.class);
        StudySessionRepository studySessionRepository = mock(StudySessionRepository.class);
        UserRepository userRepository = mock(UserRepository.class);

        ReviewReminderServiceImpl service =
                new ReviewReminderServiceImpl(reminderRepository, studySessionRepository, userRepository);

        // Arrange a StudySession with id, title, and topic
        StudySession session = new StudySession();
        session.setTopicName("D424 Capstone");
        session.setTitle("Capstone Study Block");
        session.setId(1L); // from base class StudyItem

        // Base date used in the service
        LocalDate baseDate = LocalDate.now();

        // Act
        service.generateRemindersForSession(session);

        // Capture all saved reminders
        ArgumentCaptor<ReviewReminder> captor = ArgumentCaptor.forClass(ReviewReminder.class);
        verify(reminderRepository, times(5)).save(captor.capture());

        List<ReviewReminder> saved = captor.getAllValues();
        assertEquals(5, saved.size());

        // Verify first reminder fields
        ReviewReminder first = saved.get(0);
        assertEquals("Review: Capstone Study Block", first.getTitle());
        assertEquals("D424 Capstone", first.getTopicName());
        assertEquals(1L, first.getSessionId());

        // Verify all review dates follow the spaced repetition schedule: 1, 3, 7, 14, 30 days
        LocalDate d1 = baseDate.plusDays(1);
        LocalDate d3 = baseDate.plusDays(3);
        LocalDate d7 = baseDate.plusDays(7);
        LocalDate d14 = baseDate.plusDays(14);
        LocalDate d30 = baseDate.plusDays(30);

        assertEquals(d1, saved.get(0).getReviewDate());
        assertEquals(d3, saved.get(1).getReviewDate());
        assertEquals(d7, saved.get(2).getReviewDate());
        assertEquals(d14, saved.get(3).getReviewDate());
        assertEquals(d30, saved.get(4).getReviewDate());
    }

    @Test
    void markCompleted_deletesReminderById() {
        // Mock dependencies
        ReviewReminderRepository reminderRepository = mock(ReviewReminderRepository.class);
        StudySessionRepository studySessionRepository = mock(StudySessionRepository.class);
        UserRepository userRepository = mock(UserRepository.class);

        ReviewReminderServiceImpl service =
                new ReviewReminderServiceImpl(reminderRepository, studySessionRepository, userRepository);

        Long reminderId = 42L;

        // Act
        service.markCompleted(reminderId);

        // Assert
        verify(reminderRepository, times(1)).deleteById(42L);
    }
}
