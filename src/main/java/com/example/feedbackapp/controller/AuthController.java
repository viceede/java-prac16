package com.example.feedbackapp.controller;

import com.example.feedbackapp.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
public class AuthController {
    private User registeredUser;
    private boolean isAuthenticated = false;
    private List<String> feedbackList = new ArrayList<>();
    @GetMapping("/")
    public String showRegisterPage() {
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String username,
                           @RequestParam String password,
                           Model model) {
        if (username.isEmpty() || password.isEmpty()) {
            model.addAttribute("error", "Имя пользователя и пароль не должны быть пустыми!");
            return "register";
        }
        registeredUser = new User(username, password);
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        Model model) {
        if (registeredUser == null ||
                !registeredUser.getUsername().equals(username) ||
                !registeredUser.getPassword().equals(password)) {
            model.addAttribute("error", "Неверное имя пользователя или пароль!");
            return "login";
        }
        isAuthenticated = true;
        return "redirect:/home";
    }

    @GetMapping("/home")
    public String showHomePage(Model model) {
        if (!isAuthenticated) {
            return "redirect:/login";
        }
        model.addAttribute("username", registeredUser.getUsername());
        model.addAttribute("feedbacks", feedbackList);
        return "home";
    }

    @PostMapping("/home")
    public String handleHomeForm(@RequestParam String formType,
                                 @RequestParam(required = false) String name,
                                 @RequestParam(required = false) String email,
                                 @RequestParam(required = false) String message,
                                 Model model) {

        if (!isAuthenticated) {
            return "redirect:/login";
        }

        if ("feedback".equals(formType)) {
            if (name == null || name.isEmpty() ||
                    email == null || email.isEmpty() ||
                    message == null || message.isEmpty()) {
                model.addAttribute("feedbackError", "Все поля обязательны для заполнения!");
            } else {
                String feedback = "Отзыв от " + name + " (" + email + "): " + message;
                feedbackList.add(feedback);
                model.addAttribute("feedbackSuccess", "Спасибо за ваш отзыв!");
            }
        }

        model.addAttribute("username", registeredUser.getUsername());
        model.addAttribute("feedbacks", feedbackList);
        return "home";
    }

    @GetMapping("/logout")
    public String logout() {
        isAuthenticated = false;
        return "redirect:/login";
    }
}