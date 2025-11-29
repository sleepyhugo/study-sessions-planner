package com.hugo.studysessions.repository;

import com.hugo.studysessions.model.ReviewReminder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ReviewReminderRepository extends JpaRepository<ReviewReminder, Long> {

    // All upcoming reminders from today forward (NO user filtering)
    List<ReviewReminder> findByReviewDateGreaterThanEqualOrderByReviewDateAsc(LocalDate date);

    // All reminders for one study session
    List<ReviewReminder> findBySessionIdOrderByReviewDateAsc(Long sessionId);

    // Delete all review reminders linked to a specific study session
    void deleteBySessionId(Long sessionId);

    // upcoming reminders for a list of the current user's sessions
    List<ReviewReminder> findBySessionIdInAndReviewDateGreaterThanEqualOrderByReviewDateAsc(List<Long> sessionIds, LocalDate date);
}