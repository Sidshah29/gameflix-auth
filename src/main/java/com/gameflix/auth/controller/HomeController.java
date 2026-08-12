package com.gameflix.auth.controller;

import com.gameflix.auth.model.Subscription;
import com.gameflix.auth.service.GameService;
import com.gameflix.auth.service.SubscriptionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    // The prototype UI acts on behalf of a single demo account. Real per-user
    // sessions are out of scope for this phase; the JWT API is the authenticated path.
    static final String DEMO_USER = "demo";

    private final GameService gameService;
    private final SubscriptionService subscriptionService;

    public HomeController(GameService gameService, SubscriptionService subscriptionService) {
        this.gameService = gameService;
        this.subscriptionService = subscriptionService;
    }

    // Root URL: http://localhost:8080/  -> GameFlix dashboard
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("pageTitle", "GameFlix - Dashboard");
        model.addAttribute("catalogSize", gameService.getCatalog().size());

        Subscription active = subscriptionService.getActiveSubscription(DEMO_USER).orElse(null);
        model.addAttribute("currentPlan", active != null ? active.getPlan().name() : "None");
        model.addAttribute("demoUser", DEMO_USER);
        return "index";
    }
}
