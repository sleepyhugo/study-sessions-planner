package com.hugo.studysessions.service;

import com.hugo.studysessions.model.ReviewReminder;
import com.hugo.studysessions.model.StudySession;

import java.util.List;

public interface ReviewReminderService {

    // Generate spaced-repetition review reminders for a newly created study session.
    void generateRemindersForSession(StudySession session);

    // Get all upcoming (today and future) reminders, ordered by date.
    List<ReviewReminder> getUpcomingReminders();

    // Get all reminders for a specific study session, ordered by date.
    List<ReviewReminder> getRemindersForSession(Long sessionId);

    // Mark a reminder as completed (e.g., when the student reviews the material).
    void markCompleted(Long reminderId);
}
