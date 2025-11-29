package com.hugo.studysessions.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

//b1
@MappedSuperclass
public abstract class StudyItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //Encapsulation (private)

    @NotBlank(message = "Title is required.")
    @Size(max = 100, message = "Title must be at most 100 characters.")
    private String title;

    @Size(max = 500, message = "Notes must be at most 500 characters.")
    @Column(length = 500)
    private String notes;
    private LocalDateTime createdAt;

    protected StudyItem() {
        this.createdAt = LocalDateTime.now();
    }

    protected StudyItem(String title, String notes) {
        this.title = title;
        this.notes = notes;
        this.createdAt = LocalDateTime.now();
    }

    // Getters & setters (encapsulation)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // Polymorphic method
    public abstract String getSummary();
}
