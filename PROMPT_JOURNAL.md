# Prompt Journal — GameFlix

A running log of the AI prompts that actually mattered on this project, what came
out of them, and what I changed afterward. I didn't log every little "how do I
fix this import" question — just the ones that shaped the code or that taught me
something. Newest entries are at the bottom.

---

### Entry 1 — 2026-06-11 · ChatGPT
**Prompt (summary):** Asked it to scaffold a Spring Boot 3 project with Web, JPA,
MySQL and Thymeleaf, and to explain the folder layout for controllers/services/repos.

**Generated:** A `pom.xml`, an application class, and a sample controller. The
explanation of the layered structure (controller → service → repository → entity)
was genuinely useful and I stuck with it for the whole project.

**What I changed:** The generated `pom.xml` pinned an older Boot version, so I
bumped it to 3.5.x to match what we're using in class. Also dropped a couple of
dependencies I didn't need yet (Actuator).

---

### Entry 2 — 2026-06-18 · ChatGPT
**Prompt (summary):** "Write a register/login flow in Spring Boot that stores a
BCrypt password hash, with a UserService and a REST controller."

**Generated:** `UserService` with `register`/`authenticate`, a `User` entity, and
an `AuthController`. It used constructor injection, which I liked.

**What I changed:** The first version stored the raw password on the entity and
hashed it in the controller — wrong place. I moved the encoding into the service
so the controller never touches the plaintext, and renamed the field to
`passwordHash` so it's obvious what's stored. Small thing but it bugged me that
the AI put security logic in the controller.

---

### Entry 3 — 2026-06-25 · GitHub Copilot
**Prompt (summary):** In-editor, let Copilot autocomplete the DTOs and the Bean
Validation annotations on `AuthRequest`.

**Generated:** `@NotBlank` / `@Size` on username and password.

**What I changed:** Copilot suggested `@Size(min = 8)` for the password; I dropped
it to 6 to match the demo accounts I was using, and added a message string so the
validation error is readable. Later (Module 4) I added an optional `@Email` field
here when accounts grew an email.

---

### Entry 4 — 2026-07-02 · ChatGPT
**Prompt (summary):** Asked for a JUnit 5 example that tests a service against the
real Spring context, using a Movie service as a stand-in domain.

**Generated:** A `MovieService` + `MovieServiceTest` with `@SpringBootTest`.

**What I changed:** Not much at the time — it worked and I used it as my testing
reference. In hindsight this is the entry that came back to bite me: I let the
"Movie" example sit in the codebase for weeks and it drifted away from what the
project was actually about. I ripped it out in the final realignment.

---

### Entry 5 — 2026-07-15 · Claude
**Prompt (summary):** "Here's my current repo. It has Course/Student CRUD and a
Movie example, but the project is supposed to be GameFlix. Propose entities and a
transition that reuses the auth and infra."

**Generated:** A plan mapping the domain to `UserAccount`, `Game`, and
`Subscription`, and a recommendation to keep the security/Docker/CI skeleton.

**What I changed:** Accepted most of it. I did push back on one idea — it wanted a
separate `Role` entity with a join table, and for a prototype this size that felt
like overkill, so I kept role as a plain string column on `UserAccount`. Wanted
the domain to stay readable.

---

### Entry 6 — 2026-07-22 · ChatGPT
**Prompt (summary):** "Design a Subscription entity linking a user to a plan tier
with a monthly price, and a service where a user only ever has one active
subscription."

**Generated:** A `Subscription` entity + `SubscriptionService`. The plan tiers as
an enum with prices attached was its idea and I thought it was clean.

**What I changed:** The first draft let a user pile up multiple ACTIVE rows every
time they clicked subscribe. I rewrote `subscribe()` so it updates the existing
active subscription instead of inserting a new one, and made cancel flip a status
flag rather than delete the row (so there's still a billing trail). Tested that by
subscribing twice and checking the row count.

---

### Entry 7 — 2026-07-29 · Claude
**Prompt (summary):** "Add JWT to my existing BCrypt login. I want `/login` to
return a token and one secured route `/api/me` that needs it. Keep my Thymeleaf
pages open for the demo."

**Generated:** A `JwtService` (JJWT 0.12 API), a `JwtAuthFilter` extending
`OncePerRequestFilter`, and a `SecurityConfig` that secures `/api/**` and adds the
filter.

**What I changed:** Two real fixes. First, the AI's default signing secret was too
short and JJWT throws if the HMAC key is under 256 bits — I moved the secret to a
config property (`gameflix.jwt.secret`) backed by an env var and made the fallback
long enough. Second, unauthenticated `/api` calls were redirecting to a login page
instead of returning 401, because form login was still on; I set an
`HttpStatusEntryPoint(401)` and made the API stateless. That's the kind of thing
that looks fine until you actually curl it.

---

### Entry 8 — 2026-07-30 · ChatGPT
**Prompt (summary):** "Give me a couple of curl commands and a short list of
attacks to try against a JWT-secured endpoint so I can write an attack log."

**Generated:** Ideas: call the route with no token, a tampered token, an expired
token, and a token signed with the wrong key.

**What I changed:** I actually ran each one and recorded the real responses in
`ATTACK_LOG.md` instead of trusting the AI's predicted output — good thing too,
because the "malformed token" case returned a 401 the way I wanted but only after
I fixed the filter to swallow `JwtException` instead of letting it 500. Wrote a
unit test (`JwtServiceTest`) for the wrong-key case so it can't regress.

---

### Entry 9 — 2026-08-03 · Claude
**Prompt (summary):** "Write Thymeleaf templates for a GameFlix dashboard, a game
catalog with search, and a subscription page, reusing my existing fragments head/
navbar pattern and Bootstrap."

**Generated:** `index`, `catalog`, and `subscription` templates plus a rebranded
`fragments.html` navbar.

**What I changed:** The catalog search initially hit the DB with `findByGenre`; I
wanted title search, so I swapped it to a `containing-ignore-case` query and made a
blank search fall back to the whole catalog instead of returning an empty page. I
also tweaked the plan cards so the button reads "Current" for whatever plan is
active — the AI had every button say "Select," which looked broken.

---

### Entry 10 — 2026-08-06 · ChatGPT
**Prompt (summary):** "Convert my single-stage Dockerfile into a multi-stage build
so a host like Render can build straight from source, and update my GitHub Actions
to run tests and a security scan."

**Generated:** A two-stage Dockerfile (Maven build → JRE runtime) and a CI job with
a Trivy filesystem scan step.

**What I changed:** The generated CI still had `-DskipTests`, which defeats the
point of adding a test step, so I removed that. My `@SpringBootTest` tests needed a
database, and there's no MySQL in CI, so I added a `test` profile on H2 and pointed
the tests at it. That was the missing piece that let CI actually run tests green.
I left the Trivy step as report-only (`exit-code: 0`) so a new CVE doesn't block me
the night before a deadline, but noted in the workflow how to make it enforce.

---

### Entry 11 — 2026-08-09 · Claude
**Prompt (summary):** "Review my repo for technical debt and anything that would
embarrass me in a code review."

**Generated:** A list. The two that stuck: my MySQL password was committed in
`application.properties` and `docker-compose.yml`, and the UI acted on a hardcoded
demo user with no real session.

**What I changed:** Parameterized every datasource value and the JWT secret with
env-var fallbacks so no secret is *required* to live in git, and wrote both issues
down honestly in `MAINTENANCE.md` rather than pretending the prototype is
production-ready. The demo-user shortcut I kept on purpose — wiring full per-user
sessions into every Thymeleaf page was more than this phase needed — but at least
now it's a documented decision instead of a surprise.

---

### Entry 12 — 2026-08-12 · ChatGPT + Claude
**Prompt (summary):** Final pass — asked for a data seeder that makes the app
demoable on first run (games + a demo account + one active subscription), and a
10-minute demo script.

**Generated:** `GameDataLoader` and an outline for `DEMO_SCRIPT.md`.

**What I changed:** Added a guard so restarts don't duplicate seed rows, and
seeded one game as *unavailable* on purpose so the admin publish/hide toggle has
something to show in the demo. Rewrote the demo script in my own words so I'm not
reading robotic bullet points on camera.
