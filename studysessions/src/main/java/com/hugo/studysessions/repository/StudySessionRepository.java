package com.hugo.studysessions.repository;

import com.hugo.studysessions.model.StudySession;
import com.hugo.studysessions.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudySessionRepository extends JpaRepository<StudySession, Long> {

    // search within THIS user's sessions
    List<StudySession> findByUserAndTitleContainingIgnoreCaseOrUserAndTopicNameContainingIgnoreCase(
            User user1, String title,
            User user2, String topicName
    );

    // get all sessions for this user (new)
    List<StudySession> findByUser(User user);

    // reports ONLY for this user
    List<StudySession> findByUserOrderByStartTimeDesc(User user);
}
