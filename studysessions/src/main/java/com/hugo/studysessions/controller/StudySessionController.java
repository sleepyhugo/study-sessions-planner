package com.hugo.studysessions.controller;

import com.hugo.studysessions.model.StudySession;
import com.hugo.studysessions.service.StudySessionService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/sessions")
public class StudySessionController {

    private final StudySessionService sessionService;

    public StudySessionController(StudySessionService sessionService) {
        this.sessionService = sessionService;
    }

    // List all sessions
    @GetMapping
    public String listSessions(@RequestParam(defaultValue = "0") int page, Model model) {
        int pageSize = 5; // show 5 per page
        Page<StudySession> sessionPage = sessionService.findPage(page, pageSize);

        model.addAttribute("sessions", sessionPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", sessionPage.getTotalPages());

        return "sessions-list";
    }

    // Show form to create a new session
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("session", new StudySession());
        model.addAttribute("formTitle", "Create Study Session");
        return "sessions-form";
    }

    // Show form to edit an existing session
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        StudySession session = sessionService.findById(id);

        // Prevent editing other users sessions
        if (!sessionService.isOwnedByCurrentUser(session)) {
            return "redirect:/sessions"; // Or show an error paged
        }

        model.addAttribute("session", session);
        model.addAttribute("formTitle", "Edit Study Session");
        return "sessions-form";
    }

    // Handle create/update submit
    @PostMapping("/save")
    public String saveSession(@Valid @ModelAttribute("session") StudySession session, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("formTitle", (session.getId() == null) ? "Create Study Session" : "Edit Study Session");
            return "sessions-form";
        }

        sessionService.save(session);
        return "redirect:/sessions";
    }

    // Delete a session
    @PostMapping("/delete/{id}")
    public String deleteSession(@PathVariable Long id) {

        StudySession session = sessionService.findById(id);

        // Only delete if this session belongs to the current user
        if (!sessionService.isOwnedByCurrentUser(session)) {
            return "redirect:/sessions"; // ignore or send error
        }

        sessionService.deleteById(id);
        return "redirect:/sessions";
    }

    @GetMapping("/search")
    public String search(@RequestParam("q") String keyword, Model model) {
        List<StudySession> results = sessionService.searchSessions(keyword);
        model.addAttribute("results", results);
        model.addAttribute("keyword", keyword);
        return "search-results";
    }

    @GetMapping("/pomodoro")
    public String showPomodoroTimer(Model model) {
        model.addAttribute("studySession", new StudySession());
        return "pomodoro";
    }

    @PostMapping("/pomodoro")
    public String savePomodoroSession(@ModelAttribute("studySession") StudySession studySession) {

        // Get minutes from the hidden field
        Integer durationMinutes = studySession.getDurationMinutesField();

        if (durationMinutes == null || durationMinutes <= 0) {
            // If something goes weird, default to 25 minutes
            durationMinutes = 25;
            studySession.setDurationMinutesField(durationMinutes);
        }

        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime = endTime.minusMinutes(durationMinutes);

        studySession.setEndTime(endTime);
        studySession.setStartTime(startTime);

        // Auto-generate a title since we’re not asking the user for one
        if (studySession.getTitle() == null || studySession.getTitle().isBlank()) {
            String base = (studySession.getTopicName() != null && !studySession.getTopicName().isBlank())
                    ? studySession.getTopicName()
                    : "Pomodoro session";
            studySession.setTitle("Pomodoro: " + base);
        }

        sessionService.save(studySession);

        return "redirect:/sessions";
    }
}
