package com.hugo.studysessions.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class StudySessionModelTests {

    @Test
    void createStudySession_setsFieldsCorrectly() {
        // Arrange
        StudySession session = new StudySession();

        // from StudyItem (parent class)
        session.setTitle("Capstone Work");

        session.setTopicName("D424");
        session.setDurationMinutesField(90);

        LocalDateTime startTime = LocalDateTime.of(2025, 11, 20, 18, 0);
        LocalDateTime endTime = startTime.plusMinutes(90);

        session.setStartTime(startTime);
        session.setEndTime(endTime);

        User user = new User();
        user.setId(1L);
        user.setEmail("hugo@example.com");
        user.setPassword("password123");
        user.setRole("ROLE_USER");

        session.setUser(user);

        // Act & Assert
        assertEquals("Capstone Work", session.getTitle());
        assertEquals("D424", session.getTopicName());
        assertEquals(90, session.getDurationMinutesField());
        assertEquals(startTime, session.getStartTime());
        assertEquals(endTime, session.getEndTime());

        // Duration is calculated from start/end
        assertEquals(90, session.getDurationMinutes());

        assertNotNull(session.getUser());
        assertEquals(1L, session.getUser().getId());
        assertEquals("hugo@example.com", session.getUser().getEmail());
        assertEquals("ROLE_USER", session.getUser().getRole());
    }
}
