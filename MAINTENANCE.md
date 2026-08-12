# Maintenance & Technical Debt

Honest notes on what's deliberately simplified in this prototype and what a
production build would need. Keeping this visible so nothing is a surprise in a
code review.

## Known technical debt

1. **UI acts on a single demo user.** The Thymeleaf pages (dashboard,
   subscription) operate on a hardcoded `demo` account instead of the logged-in
   user. The authenticated path in this prototype is the JWT API (`/api/me`).
   *Next step:* carry the JWT (or a session) into the UI and read the current user
   from the security context on every page.

2. **Deployed data is in-memory.** The `cloud` profile uses H2, so catalog/account
   data resets on every restart (re-seeded by `GameDataLoader`). Fine for a demo;
   a real deployment needs a managed MySQL/Postgres and Flyway migrations.

3. **Dev credentials have defaults in git.** `application.properties` and
   `docker-compose.yml` carry a local MySQL password and a dev JWT secret as
   *fallbacks*. Every one is overridable by an environment variable, and real
   deployments must set `SPRING_DATASOURCE_PASSWORD` and `JWT_SECRET`. The local
   MySQL password should be rotated once the course is over.

4. **JWTs aren't revocable.** A token is valid until it expires (default 60 min).
   No logout/blacklist. *Next step:* short-lived access tokens + a refresh token,
   or a server-side token store.

5. **Admin routes aren't role-gated.** `/admin` is open for the demo. The
   `UserAccount.role` field exists; wiring `hasRole("ADMIN")` into `SecurityConfig`
   is the follow-up.

## Maintenance routine

- CI (`.github/workflows/ci.yml`) runs the test suite and a Trivy dependency scan
  on every push to `main`. Review the Trivy output periodically; flip its
  `exit-code` to `1` to make HIGH/CRITICAL findings block merges.
- Keep the Spring Boot parent version current for security patches.
