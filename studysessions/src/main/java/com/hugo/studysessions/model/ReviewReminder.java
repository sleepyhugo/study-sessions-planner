package com.hugo.studysessions.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.LocalDate;

//b1
@Entity
@Table(name = "review_reminders")
public class ReviewReminder extends StudyItem {

    private String topicName;
    private LocalDate reviewDate;
    private Long sessionId;

    public ReviewReminder() {
        super();
    }

    public ReviewReminder(String title, String notes, String topicName, LocalDate reviewDate, Long sessionId) {
        super(title, notes);
        this.topicName = topicName;
        this.reviewDate = reviewDate;
        this.sessionId = sessionId;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public LocalDate getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(LocalDate reviewDate) {
        this.reviewDate = reviewDate;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public String getSummary() {
        return "Review '" + topicName + "' on " + reviewDate;
    }
}
