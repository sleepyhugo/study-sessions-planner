package com.hugo.studysessions.controller;

import com.hugo.studysessions.dto.RegistrationDto;
import com.hugo.studysessions.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    // show login page
    @GetMapping("/login")
    public String showLogin() {
        return "login";
    }

    // show registration page
    @GetMapping("/register")
    public String showRegister(Model model) {
        model.addAttribute("registrationForm", new RegistrationDto());
        return "registration";
    }

    // handle registration submit
    @PostMapping("/register")
    public String handleRegister(@ModelAttribute("registrationForm") RegistrationDto form,
                                 Model model) {

        // simple validations
        if (form.getEmail() == null || form.getEmail().isBlank()
                || form.getPassword() == null || form.getPassword().isBlank()) {
            model.addAttribute("errorMessage", "Email and password are required.");
            return "registration";
        }

        if (!form.getPassword().equals(form.getConfirmPassword())) {
            model.addAttribute("errorMessage", "Passwords do not match.");
            return "registration";
        }

        if (userService.emailExists(form.getEmail())) {
            model.addAttribute("errorMessage", "An account with that email already exists.");
            return "registration";
        }

        // create user
        userService.registerNewUser(form);

        // redirect to login with a flag so we can show a nice message
        return "redirect:/login?registered";
    }
}
