package com.gameflix.auth.security;

import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Pure unit test for the JWT round-trip - no Spring context, so it runs fast in
 * CI. Verifies a token issued for a username reads back as that username, and
 * that a token signed with a different secret is rejected.
 */
class JwtServiceTest {

    private static final String SECRET = "unit-test-secret-key-at-least-32-bytes!!";

    @Test
    void generatedToken_roundTripsToSameUsername() {
        JwtService service = new JwtService(SECRET, 60);

        String token = service.generateToken("alice");

        Assertions.assertEquals("alice", service.extractUsername(token),
                "Extracted subject should match the username the token was issued for");
    }

    @Test
    void tokenSignedWithDifferentSecret_isRejected() {
        JwtService issuer = new JwtService(SECRET, 60);
        JwtService attacker = new JwtService("a-totally-different-secret-key-32-bytes!!", 60);

        String token = issuer.generateToken("bob");

        Assertions.assertThrows(JwtException.class, () -> attacker.extractUsername(token),
                "A token verified against the wrong key must fail signature validation");
    }
}
