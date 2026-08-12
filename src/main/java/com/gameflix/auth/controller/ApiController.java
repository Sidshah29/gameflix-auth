package com.gameflix.auth.controller;

import com.gameflix.auth.model.Subscription;
import com.gameflix.auth.model.UserAccount;
import com.gameflix.auth.service.SubscriptionService;
import com.gameflix.auth.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * JWT-secured API. SecurityConfig requires an authenticated principal for
 * anything under /api/**, so reaching these methods proves a valid token was
 * presented. The username comes from the token's subject via the SecurityContext.
 */
@RestController
@RequestMapping("/api")
public class ApiController {

    private final UserService userService;
    private final SubscriptionService subscriptionService;

    public ApiController(UserService userService, SubscriptionService subscriptionService) {
        this.userService = userService;
        this.subscriptionService = subscriptionService;
    }

    /** Returns the caller's own profile and current plan - the protected route. */
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> me(Authentication authentication) {
        String username = authentication.getName();

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("username", username);

        UserAccount user = userService.findByUsername(username).orElse(null);
        body.put("email", user != null ? user.getEmail() : null);
        body.put("role", user != null ? user.getRole() : "USER");

        Subscription active = subscriptionService.getActiveSubscription(username).orElse(null);
        body.put("plan", active != null ? active.getPlan().name() : "NONE");
        body.put("monthlyPrice", active != null ? active.getMonthlyPrice() : 0.0);

        return ResponseEntity.ok(body);
    }
}
