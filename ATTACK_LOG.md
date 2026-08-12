# Attack Log — GameFlix Auth & JWT

Security probing I ran against the authentication and JWT-secured routes, with the
actual behavior observed and what it means. All tests were run locally against
`http://localhost:8080` with the seeded demo account (`demo` / `demo123`).

The secured route under test is `GET /api/me`, which `SecurityConfig` requires an
authenticated principal for. The token is obtained from `POST /login`.

---

## 1. Access the protected route with no token

**Request**
```bash
curl -i localhost:8080/api/me
```
**Result:** `401 Unauthorized` (empty body).
**Why:** `SecurityConfig` requires authentication for `/api/**` and uses an
`HttpStatusEntryPoint(401)`, so unauthenticated calls are rejected cleanly instead
of redirecting to a login page. ✅ Expected.

---

## 2. Access with a garbage / malformed token

**Request**
```bash
curl -i localhost:8080/api/me -H "Authorization: Bearer not-a-real-jwt"
```
**Result:** `401 Unauthorized`.
**Why:** `JwtAuthFilter` calls `jwtService.extractUsername`, which throws a
`JwtException` on the malformed string. The filter catches it, clears the security
context, and lets the chain reject the request. Early on this returned a **500**
because the exception wasn't caught — fixed by wrapping the parse in try/catch. ✅

---

## 3. Access with a token signed by a different key (forgery)

**Setup:** Generated a token for user `demo` using a *different* secret (simulating
an attacker who doesn't know `JWT_SECRET`), then presented it.

**Result:** `401 Unauthorized`.
**Why:** JJWT verifies the HMAC signature with the server's key
(`verifyWith(key)`). A signature that doesn't match is rejected before the claims
are trusted. This is covered by an automated test,
`JwtServiceTest.tokenSignedWithDifferentSecret_isRejected`. ✅

---

## 4. Access with an expired token

**Setup:** Set `gameflix.jwt.expiry-minutes=0` in a throwaway run, logged in, waited,
and reused the token.

**Result:** `401 Unauthorized`.
**Why:** JJWT throws `ExpiredJwtException` (a `JwtException` subclass) during parse,
which the filter treats the same as any invalid token. ✅

---

## 5. Valid token (control case)

**Request**
```bash
TOKEN=$(curl -s -X POST localhost:8080/login \
  -H "Content-Type: application/json" \
  -d '{"username":"demo","password":"demo123"}' | jq -r .token)
curl -s localhost:8080/api/me -H "Authorization: Bearer $TOKEN"
```
**Result:** `200 OK` with `{"username":"demo","email":...,"role":"USER","plan":"STANDARD",...}`.
**Why:** Valid signature + unexpired → the filter populates the security context and
`ApiController` reads the username from it. ✅

---

## 6. Login with a wrong password

**Request**
```bash
curl -i -X POST localhost:8080/login \
  -H "Content-Type: application/json" \
  -d '{"username":"demo","password":"wrongpass"}'
```
**Result:** `401 Unauthorized`, body `{"message":"Invalid username or password"}`.
**Why:** `UserService.authenticate` uses `passwordEncoder.matches` against the
stored BCrypt hash. The same generic message is returned for both "bad password"
and "no such user," so the endpoint doesn't leak which usernames exist. ✅

---

## 7. Register a duplicate username

**Request:** POST `/register` twice with `{"username":"demo",...}`.
**Result:** Second call → `409 Conflict`, `{"message":"Username already exists"}`.
**Why:** `UserService.register` checks for an existing username and throws
`DuplicateUsernameException`, mapped to 409 by an `@ExceptionHandler`. The DB
`unique` constraint on `username` is the backstop. ✅

---

## 8. SQL injection in the login username

**Request**
```bash
curl -i -X POST localhost:8080/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin'"'"' OR '"'"'1'"'"'='"'"'1","password":"x"}'
```
**Result:** `401 Unauthorized` — treated as a literal (non-existent) username.
**Why:** All queries go through Spring Data JPA / parameter binding
(`findByUsername`), so the payload is bound as a value, never concatenated into
SQL. No injection. ✅

---

## 9. Weak input rejected before it reaches the DB

**Request:** POST `/register` with a 3-character password.
**Result:** `400 Bad Request` from Bean Validation (`@Size(min = 6)` on
`AuthRequest.password`).
**Why:** `@Valid` on the controller parameter runs the constraints before any
service logic. ✅

---

## 10. Confirm passwords are never stored in plaintext

**Check:** Inspected the `user_accounts` table after registering.
**Result:** `password_hash` holds a `$2a$...` BCrypt hash; the plaintext appears
nowhere. ✅

---

## Summary

| # | Attack | Outcome | Status |
|---|--------|---------|--------|
| 1 | No token | 401 | Pass |
| 2 | Malformed token | 401 (was 500 before fix) | Pass |
| 3 | Forged signature | 401 | Pass |
| 4 | Expired token | 401 | Pass |
| 5 | Valid token | 200 | Pass |
| 6 | Wrong password | 401, generic message | Pass |
| 7 | Duplicate register | 409 | Pass |
| 8 | SQL injection | 401, no injection | Pass |
| 9 | Weak password | 400 validation | Pass |
| 10 | Plaintext password | Only BCrypt hash stored | Pass |

**Known limitations (documented, not fixed in this prototype):** the JWT is not
revocable before expiry (no server-side token store), CSRF is disabled because the
API is stateless and token-based, and the Thymeleaf UI itself is open for the demo
rather than gated behind the JWT. See `MAINTENANCE.md`.
