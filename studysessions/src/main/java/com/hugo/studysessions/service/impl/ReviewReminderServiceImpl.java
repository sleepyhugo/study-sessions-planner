package com.hugo.studysessions.service.impl;

import com.hugo.studysessions.model.ReviewReminder;
import com.hugo.studysessions.model.StudySession;
import com.hugo.studysessions.model.User;
import com.hugo.studysessions.repository.ReviewReminderRepository;
import com.hugo.studysessions.repository.StudySessionRepository;
import com.hugo.studysessions.repository.UserRepository;
import com.hugo.studysessions.service.ReviewReminderService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewReminderServiceImpl implements ReviewReminderService {

    private final ReviewReminderRepository reminderRepository;
    private final StudySessionRepository studySessionRepository;
    private final UserRepository userRepository;

    public ReviewReminderServiceImpl(ReviewReminderRepository reminderRepository,
                                     StudySessionRepository studySessionRepository,
                                     UserRepository userRepository) {   // Updated constructor
        this.reminderRepository = reminderRepository;
        this.studySessionRepository = studySessionRepository;
        this.userRepository = userRepository;
    }

    // Helper: get the currently logged-in User entity
    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName(); // username = email

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Logged-in user not found: " + email));
    }

    // Create spaced-repetition review reminders for a new study session.
    // Schedule: 1, 3, 7, 14, 30 days after the session date.
    @Override
    @Transactional
    public void generateRemindersForSession(StudySession session) {
        if (session == null || session.getId() == null) {
            return;
        }

        // Simple spaced repetition schedule (days after session)
        List<Integer> days = Arrays.asList(1, 3, 7, 14, 30);

        // Base date: when the session was created (fallback to today)
        LocalDate baseDate;
        if (session.getCreatedAt() != null) {
            baseDate = session.getCreatedAt().toLocalDate();
        } else {
            baseDate = LocalDate.now();
        }

        for (Integer d : days) {
            LocalDate reviewDate = baseDate.plusDays(d);

            String title = "Review: " +
                    (session.getTitle() != null ? session.getTitle() : session.getTopicName());

            String notes = "Auto-generated spaced repetition reminder.";

            ReviewReminder reminder = new ReviewReminder(
                    title,
                    notes,
                    session.getTopicName(),
                    reviewDate,
                    session.getId()
            );

            reminderRepository.save(reminder);
        }
    }

    @Override
    public List<ReviewReminder> getUpcomingReminders() {
        // Only show reminders for the current user's sessions
        User user = getCurrentUser();

        // Get this user's sessions
        List<StudySession> sessionsForUser = studySessionRepository.findByUser(user);
        if (sessionsForUser.isEmpty()) {
            return Collections.emptyList();
        }

        // Collect their session IDs
        List<Long> sessionIds = sessionsForUser.stream()
                .map(StudySession::getId)
                .collect(Collectors.toList());

        return reminderRepository.findBySessionIdInAndReviewDateGreaterThanEqualOrderByReviewDateAsc(sessionIds, LocalDate.now());
    }

    @Override
    public List<ReviewReminder> getRemindersForSession(Long sessionId) {
        // Security: ensure the session belongs to the current user
        User user = getCurrentUser();
        StudySession session = studySessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid session id: " + sessionId));

        if (session.getUser() == null || !session.getUser().getId().equals(user.getId())) {
            // Not your session return nothing (or throw)
            return Collections.emptyList();
        }

        return reminderRepository.findBySessionIdOrderByReviewDateAsc(sessionId);
    }

    // "Complete" a reminder by removing it from the list
    @Override
    @Transactional
    public void markCompleted(Long reminderId) {
        reminderRepository.deleteById(reminderId);
    }
}
