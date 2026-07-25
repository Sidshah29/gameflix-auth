package com.gameflix.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * GameFlix Account Service entry point.
 * Exposes /register and /login for the prototype (Module 4, Task 4.a).
 */
@SpringBootApplication
public class GameflixAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(GameflixAuthApplication.class, args);
    }
}
