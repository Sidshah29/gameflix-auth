package com.gameflix.auth.dto;

/**
 * Simple message envelope used by /register and /login.
 * A future iteration will replace the login response with a JWT payload (Task 4.b).
 */
public class AuthResponse {

    private String message;

    public AuthResponse() {
    }

    public AuthResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
