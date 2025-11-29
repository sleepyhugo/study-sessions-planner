package com.hugo.studysessions.controller;

import com.hugo.studysessions.service.StudySessionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@Controller
public class ReportController {

    private final StudySessionService sessionService;

    public ReportController(StudySessionService sessionService) {
        this.sessionService = sessionService;
    }

    @GetMapping("/reports/sessions")
    public String sessionsReport(Model model) {
        Map<String, Object> data = sessionService.getSessionsReportData();
        model.addAllAttributes(data);
        return "report-sessions";
    }
}
