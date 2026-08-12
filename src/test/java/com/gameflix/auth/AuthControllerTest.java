package com.gameflix.auth;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Smoke test: verifies the whole Spring context (security, JPA, JWT, data
 * loader) boots against the in-memory H2 test database.
 */
@SpringBootTest
@ActiveProfiles("test")
class AuthControllerTest {

    @Test
    void contextLoads() {
    }
}
