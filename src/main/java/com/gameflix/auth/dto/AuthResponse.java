package com.gameflix.auth.dto;

/**
 * Message envelope for /register and /login.
 * On a successful login the token field carries the JWT the client then sends
 * back as "Authorization: Bearer &lt;token&gt;" to reach the secured /api routes.
 * The token stays null for register and for failed logins.
 */
public class AuthResponse {

    private String message;
    private String token;

    public AuthResponse() {
    }

    public AuthResponse(String message) {
        this.message = message;
    }

    public AuthResponse(String message, String token) {
        this.message = message;
        this.token = token;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
