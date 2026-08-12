package com.gameflix.auth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Serves the static-ish auth pages. The forms on these pages call the JSON
 * /register and /login endpoints with a little fetch() so the demo can show the
 * real REST responses (including the JWT) in the browser.
 */
@Controller
public class PageController {

    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("pageTitle", "GameFlix - Login");
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("pageTitle", "GameFlix - Register");
        return "register";
    }
}
