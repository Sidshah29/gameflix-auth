package com.gameflix.auth.controller;

import com.gameflix.auth.dto.AuthRequest;
import com.gameflix.auth.dto.AuthResponse;
import com.gameflix.auth.service.UserService;
import com.gameflix.auth.service.UserService.DuplicateUsernameException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST endpoints for the assignment:
 *   POST /register  - create an account
 *   POST /login     - verify credentials
 */
@RestController
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody AuthRequest req) {
        userService.register(req.getUsername(), req.getPassword());
        return ResponseEntity.ok(new AuthResponse("User registered successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest req) {
        boolean ok = userService.authenticate(req.getUsername(), req.getPassword());
        if (ok) {
            return ResponseEntity.ok(new AuthResponse("Login successful"));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new AuthResponse("Invalid username or password"));
    }

    @ExceptionHandler(DuplicateUsernameException.class)
    public ResponseEntity<AuthResponse> handleDuplicate(DuplicateUsernameException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new AuthResponse("Username already exists"));
    }
}
