package com.gameflix.auth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    // Root URL: http://localhost:8080/
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("pageTitle", "GameFlix - Home");
        // Must match src/main/resources/templates/index.html
        return "index";
    }
}