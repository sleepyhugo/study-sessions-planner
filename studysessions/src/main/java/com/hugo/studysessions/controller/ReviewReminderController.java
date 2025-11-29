package com.hugo.studysessions.controller;

import com.hugo.studysessions.service.ReviewReminderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/reviews")
public class ReviewReminderController {

    private final ReviewReminderService reviewReminderService;

    public ReviewReminderController(ReviewReminderService reviewReminderService) {
        this.reviewReminderService = reviewReminderService;
    }

    // Show all upcoming review reminders for the CURRENT logged-in user.
    // URL: GET /reviews/upcoming
    @GetMapping("/upcoming")
    public String upcomingReviews(Model model) {
        model.addAttribute("reminders", reviewReminderService.getUpcomingReminders());
        return "reviews/upcoming";  // templates/reviews/upcoming.html
    }

    // Mark a specific reminder as completed for the current user.
    // URL: POST /reviews/{id}/complete
    @PostMapping("/{id}/complete")
    public String completeReminder(@PathVariable Long id) {
        reviewReminderService.markCompleted(id);
        return "redirect:/reviews/upcoming";
    }
}
