package com.hugo.studysessions.service;

import com.hugo.studysessions.model.ReviewReminder;
import com.hugo.studysessions.model.StudySession;
import com.hugo.studysessions.repository.ReviewReminderRepository;
import com.hugo.studysessions.repository.StudySessionRepository;
import com.hugo.studysessions.repository.UserRepository;
import com.hugo.studysessions.service.impl.ReviewReminderServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ReviewReminderServiceTests {

    @Test
    void generateRemindersForSession_createsFiveSpacedReminders() {
        // Mock dependencies
        ReviewReminderRepository reminderRepository = mock(ReviewReminderRepository.class);
        StudySessionRepository studySessionRepository = mock(StudySessionRepository.class);
        UserRepository userRepository = mock(UserRepository.class);

        ReviewReminderServiceImpl service =
                new ReviewReminderServiceImpl(reminderRepository, studySessionRepository, userRepository);

        // Arrange: a StudySession with id, title, topic
        StudySession session = new StudySession();
        session.setId(1L); // from StudyItem base class
        session.setTitle("Capstone Study Block");
        session.setTopicName("D424 Capstone");

        // Since createdAt is null, service will fallback to today's date
        LocalDate baseDate = LocalDate.now();

        // Act
        service.generateRemindersForSession(session);

        // Capture saved reminders
        ArgumentCaptor<ReviewReminder> captor = ArgumentCaptor.forClass(ReviewReminder.class);
        verify(reminderRepository, times(5)).save(captor.capture());

        List<ReviewReminder> saved = captor.getAllValues();
        assertEquals(5, saved.size());

        // Verify title, topic, and session ID
        ReviewReminder first = saved.get(0);
        assertEquals(1L, first.getSessionId());
        assertEquals("D424 Capstone", first.getTopicName());
        assertEquals("Review: Capstone Study Block", first.getTitle());

        // Expected spaced repetition dates: 1, 3, 7, 14, 30 days
        assertEquals(baseDate.plusDays(1), saved.get(0).getReviewDate());
        assertEquals(baseDate.plusDays(3), saved.get(1).getReviewDate());
        assertEquals(baseDate.plusDays(7), saved.get(2).getReviewDate());
        assertEquals(baseDate.plusDays(14), saved.get(3).getReviewDate());
        assertEquals(baseDate.plusDays(30), saved.get(4).getReviewDate());
    }

    @Test
    void markCompleted_deletesReminderById() {
        // Mock dependencies
        ReviewReminderRepository reminderRepository = mock(ReviewReminderRepository.class);
        StudySessionRepository studySessionRepository = mock(StudySessionRepository.class);
        UserRepository userRepository = mock(UserRepository.class);

        ReviewReminderServiceImpl service = new ReviewReminderServiceImpl(reminderRepository, studySessionRepository, userRepository);

        Long reminderId = 42L;

        // Act
        service.markCompleted(reminderId);

        // Assert: deleteById was called with correct id
        verify(reminderRepository, times(1)).deleteById(42L);
    }
}
