package com.hugo.studysessions.controller;

import com.hugo.studysessions.service.StudySessionService;
import com.hugo.studysessions.service.ReviewReminderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final StudySessionService studySessionService;
    private final ReviewReminderService reviewReminderService;

    public DashboardController(StudySessionService studySessionService, ReviewReminderService reviewReminderService) {
        this.studySessionService = studySessionService;
        this.reviewReminderService = reviewReminderService;
    }

    @GetMapping("/dashboard")
    public String showDashboard(Model model) {

        long totalStudyMinutes = studySessionService.getTotalStudyMinutes();
        int currentStreakDays = studySessionService.getCurrentStreakDays();
        int upcomingReviewsCount = reviewReminderService.getUpcomingReminders().size();


        model.addAttribute("totalStudyMinutes", totalStudyMinutes);
        model.addAttribute("currentStreakDays", currentStreakDays);
        model.addAttribute("upcomingReviewsCount", upcomingReviewsCount);

        return "dashboard";
    }
}
