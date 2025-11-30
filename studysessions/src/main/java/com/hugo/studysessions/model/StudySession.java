package com.hugo.studysessions.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.Duration;
import java.time.LocalDateTime;

@Entity
@Table(name = "study_sessions")
public class StudySession extends StudyItem {

    @NotBlank(message = "Topic name is required.")
    private String topicName;
    // Stores Pomodoro duration in minutes from the form
    private Integer durationMinutesField;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public StudySession() {
        super();
    }

    public StudySession(String title, String notes, String topicName, LocalDateTime startTime, LocalDateTime endTime) {
        super(title, notes);
        this.topicName = topicName;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public long getDurationMinutes() {
        if (durationMinutesField != null && durationMinutesField > 0) {
            return durationMinutesField;
        }

        if (startTime == null || endTime == null) return 0;
        return Duration.between(startTime, endTime).toMinutes();
    }

    @Override
    public String getSummary() {
        return "Study Session on '" + topicName + "' for " + getDurationMinutes() + " minutes.";
    }

    public Integer getDurationMinutesField() {
        return durationMinutesField;
    }

    public void setDurationMinutesField(Integer durationMinutesField) {
        this.durationMinutesField = durationMinutesField;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
