# 10-Minute Demo Script — GameFlix

Everything you need to show, in order, with the exact URLs and what to say. Times
are a guide. Start the app first (`mvn spring-boot:run`, or the deployed URL) and
have the seeded demo account ready (`demo` / `demo123`).

---

## 0:00 – 0:45 · Intro
> "This is GameFlix, a video game subscription platform — the idea is Netflix, but
> for games. It's a Spring Boot 3 app with a Thymeleaf UI, MySQL for storage, and a
> JWT-secured API. I'll walk through the three main user flows, then the security
> layer, the tests, and the CI pipeline."

---

## 0:45 – 2:00 · Flow 1: Dashboard + Catalog
1. Open **`http://localhost:8080/`** — the dashboard.
   > "The dashboard shows how many games are available and the current plan for the
   > active account."
2. Open **`http://localhost:8080/catalog`**.
   > "Here's the catalog — each card is a game with its platform and genre."
3. In the search box type **`portal`** or **`hades`** and hit Search
   (URL becomes `/catalog?q=hades`).
   > "Search is case-insensitive and only ever returns games that are currently
   > available — hidden titles never show up here."

---

## 2:00 – 3:15 · Flow 2: Subscriptions
1. Open **`http://localhost:8080/subscription`**.
   > "This is the subscription page for the demo user. Right now they're on the
   > Standard plan at $9.99."
2. Click **Select** on **Premium**.
   > "Switching plans updates the existing subscription rather than stacking a new
   > one — a user only ever has one active plan."
3. Point out the price/badge changed, then optionally click **Cancel subscription**
   and show the "no active subscription" state, then re-subscribe.

---

## 3:15 – 4:30 · Flow 3: Admin
1. Open **`http://localhost:8080/admin`**.
   > "Admins manage the library here. Notice 'Unreleased Beta' is marked
   > unavailable — that's why it didn't appear in the catalog earlier."
2. Click **Publish** on that row, then reload **`/catalog`** to show it now appears.
3. Back on `/admin`, use the **Add a game** form to add one live (e.g. Title
   `Tetris`, Platform `Switch`, Genre `Puzzle`), show it in the table.

---

## 4:30 – 6:30 · Security: register, login, JWT
1. Open **`http://localhost:8080/login`**.
   > "The login page posts to the real REST endpoint and shows the response."
2. Click **Login** with the pre-filled demo credentials. Point at the JSON:
   > "A successful login returns a signed JWT. The client sends this back as a
   > Bearer token to reach the protected API."
3. In a terminal, show the secured route:
   ```bash
   curl -i localhost:8080/api/me
   ```
   > "No token — 401."
   ```bash
   TOKEN=$(curl -s -X POST localhost:8080/login -H "Content-Type: application/json" \
     -d '{"username":"demo","password":"demo123"}' | jq -r .token)
   curl -s localhost:8080/api/me -H "Authorization: Bearer $TOKEN"
   ```
   > "With a valid token — 200, and it returns the caller's own profile and plan.
   > I documented the full set of attacks I tried in ATTACK_LOG.md."

---

## 6:30 – 8:30 · Code explain + live edit (the 2-minute segment)

**Open `src/main/java/com/gameflix/auth/service/GameService.java` and explain the
`search` method line by line:**

> "This is the catalog search. It takes the query string from the controller.
> First, if the query is null or blank, I return the whole catalog — that way an
> empty search box shows everything instead of a blank page. Otherwise I call the
> repository's `findByTitleContainingIgnoreCase`, which is a case-insensitive
> title match, and then I stream the results and filter down to only games where
> `isAvailable()` is true. So even if an unavailable game's title matches, it never
> reaches a subscriber."

**Now make this safe live edit** — trim the query and guard length so a huge input
can't be thrown at the database:

Change the guard line from:
```java
if (query == null || query.isBlank()) {
    return getCatalog();
}
return gameRepository.findByTitleContainingIgnoreCase(query.trim())
```
to:
```java
if (query == null || query.isBlank()) {
    return getCatalog();
}
String cleaned = query.trim();
if (cleaned.length() > 100) {
    cleaned = cleaned.substring(0, 100); // guard against absurdly long search input
}
return gameRepository.findByTitleContainingIgnoreCase(cleaned)
```

> "I'm tightening the input handling — trimming the query and capping it at 100
> characters so someone can't paste a giant string into the search box. Small
> change, but it's the kind of validation that belongs in the service."

Save, let Spring DevTools reload (or restart), and re-run a search to show it still
works.

---

## 8:30 – 9:30 · Tests + CI
1. Run:
   ```bash
   mvn test
   ```
   > "Tests run against in-memory H2, so no database setup is needed. GameServiceTest
   > checks that unavailable games stay hidden and that search is case-insensitive.
   > JwtServiceTest checks a token round-trips and that a forged token is rejected."
2. Open the repo's **Actions** tab on GitHub.
   > "Every push runs the same build, the tests, and a Trivy security scan, then
   > builds the Docker image. Here's the last green run."

---

## 9:30 – 10:00 · Wrap-up
> "So that's GameFlix: account management with a JWT-secured API, a searchable game
> catalog, subscription management, admin tools, tested logic, and a Dockerized CI
> pipeline. My prompt journal and reflection cover how I used AI and where I had to
> correct it. Thanks for watching."

---

### Pre-flight checklist
- [ ] App running (local or deployed), reachable in the browser
- [ ] `demo` / `demo123` works on the login page
- [ ] Terminal open with `curl` and `jq` available for the JWT demo
- [ ] IDE open to `GameService.java` for the live edit
- [ ] GitHub Actions tab open to a green run
