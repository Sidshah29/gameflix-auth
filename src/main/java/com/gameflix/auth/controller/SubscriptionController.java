package com.gameflix.auth.controller;

import com.gameflix.auth.model.Subscription;
import com.gameflix.auth.service.SubscriptionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    // GET /subscription -> current plan for the demo user + the available tiers
    @GetMapping("/subscription")
    public String viewSubscription(Model model) {
        Subscription active = subscriptionService
                .getActiveSubscription(HomeController.DEMO_USER)
                .orElse(null);

        model.addAttribute("subscription", active);
        model.addAttribute("plans", Subscription.Plan.values());
        model.addAttribute("demoUser", HomeController.DEMO_USER);
        model.addAttribute("pageTitle", "GameFlix - My Subscription");
        return "subscription";
    }

    // POST /subscription/subscribe -> start or switch plan, then reload the page
    @PostMapping("/subscription/subscribe")
    public String subscribe(@RequestParam("plan") Subscription.Plan plan) {
        subscriptionService.subscribe(HomeController.DEMO_USER, plan);
        return "redirect:/subscription";
    }

    // POST /subscription/cancel -> cancel the active plan
    @PostMapping("/subscription/cancel")
    public String cancel() {
        subscriptionService.cancel(HomeController.DEMO_USER);
        return "redirect:/subscription";
    }
}
