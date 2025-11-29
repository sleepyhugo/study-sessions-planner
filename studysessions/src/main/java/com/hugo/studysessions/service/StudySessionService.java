package com.hugo.studysessions.service;

import com.hugo.studysessions.model.StudySession;
import com.hugo.studysessions.model.User;
import com.hugo.studysessions.repository.StudySessionRepository;
import com.hugo.studysessions.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.hugo.studysessions.repository.ReviewReminderRepository;

import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StudySessionService {
    private final StudySessionRepository repository;
    private final ReviewReminderService reviewReminderService;
    private final ReviewReminderRepository reviewReminderRepository;
    private final UserRepository userRepository;

    public StudySessionService(StudySessionRepository repository, ReviewReminderService reviewReminderService, ReviewReminderRepository reviewReminderRepository, UserRepository userRepository) {
        this.repository = repository;
        this.reviewReminderService = reviewReminderService;
        this.reviewReminderRepository = reviewReminderRepository;
        this.userRepository = userRepository;
    }

    // helper: get the currently logged-in User entity
    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName(); // username = email from login form

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Logged-in user not found: " + email));
    }

    // Get all sessions for the current user
    public List<StudySession> findAll() {
        User user = getCurrentUser();
        return repository.findByUser(user);
    }

    // Find one session by id
    public StudySession findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid session id: " + id));
    }

    // Create or update session
    public StudySession save(StudySession session) {
        boolean isNew = (session.getId() == null);   // if no ID yet, this is a new session

        // Always attach the current user to the session
        session.setUser(getCurrentUser());

        StudySession saved = repository.save(session);

        // Only generate reminders when the session is first created
        if (isNew) {
            reviewReminderService.generateRemindersForSession(saved);
        }

        return saved;
    }

    // Delete session and its review reminders
    @Transactional
    public void deleteById(Long id) {
        // First delete all reminders linked to this session
        reviewReminderRepository.deleteBySessionId(id);

        // Then delete the session itself
        repository.deleteById(id);
    }

    // Search within current user's sessions
    public List<StudySession> searchSessions(String keyword) {
        User user = getCurrentUser();
        return repository
                .findByUserAndTitleContainingIgnoreCaseOrUserAndTopicNameContainingIgnoreCase(
                        user, keyword,
                        user, keyword
                );
    }

    // Calculate current study streak in days for current user
    public int getCurrentStreakDays() {
        User user = getCurrentUser();
        List<StudySession> sessions = repository.findByUserOrderByStartTimeDesc(user);

        if (sessions.isEmpty()) {
            return 0;
        }

        // Collect distinct study dates in order (most recent first)
        List<LocalDate> dates = new ArrayList<>();
        for (StudySession session : sessions) {
            if (session.getStartTime() == null) {
                continue;
            }
            LocalDate date = session.getStartTime().toLocalDate();
            if (!dates.contains(date)) {
                dates.add(date);
            }
        }

        if (dates.isEmpty()) {
            return 0;
        }

        int streak = 0;
        // Most recent day you studied
        LocalDate expected = dates.get(0);

        for (LocalDate date : dates) {
            if (date.equals(expected)) {
                streak++;
                expected = expected.minusDays(1); // expect the previous calendar day next
            } else {
                // gap found -> streak ends
                break;
            }
        }

        return streak;
    }

    public Map<String, Object> getSessionsReportData() {
        User user = getCurrentUser();
        List<StudySession> sessions = repository.findByUserOrderByStartTimeDesc(user);

        long totalMinutes = sessions.stream().mapToLong(StudySession::getDurationMinutes).sum();

        Map<String, Object> reportData = new HashMap<>();
        reportData.put("sessions", sessions);
        reportData.put("totalMinutes", totalMinutes);
        reportData.put("generatedAt", LocalDateTime.now());
        reportData.put("title", "Study Sessions Report");

        return reportData;
    }

    // Helper for dashboard total study time
    public long getTotalStudyMinutes() {
        Map<String, Object> report = getSessionsReportData();
        Object total = report.get("totalMinutes");

        if (total instanceof Long) {
            return (Long) total;
        }
        if (total instanceof Integer) {
            return ((Integer) total).longValue();
        }
        return 0L;
    }

    // Paginated sessions for the current user
    public Page<StudySession> findPage(int page, int size) {
        User user = getCurrentUser();
        List<StudySession> allForUser = repository.findByUserOrderByStartTimeDesc(user);

        int fromIndex = page * size;
        if (fromIndex >= allForUser.size()) {
            return Page.empty();
        }

        int toIndex = Math.min(fromIndex + size, allForUser.size());
        List<StudySession> pageContent = allForUser.subList(fromIndex, toIndex);

        Pageable pageable = PageRequest.of(page, size);
        return new PageImpl<>(pageContent, pageable, allForUser.size());
    }

    public boolean isOwnedByCurrentUser(StudySession session) {
        User currentUser = getCurrentUser();
        return session.getUser().getId().equals(currentUser.getId());
    }
}
