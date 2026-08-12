# GameFlix

A prototype video game subscription platform — think "Netflix for games." Users
browse a game catalog, pick a subscription plan, and manage their account. Admins
manage the game library. Built as a Spring Boot 3 application for HCDD 412.

## Features

- **Account management** — register/login as REST endpoints, passwords stored as BCrypt hashes.
- **JWT-secured API** — `POST /login` returns a signed JWT; `GET /api/me` requires it.
- **Game catalog** — browse and search available titles.
- **Subscriptions** — three plan tiers (Basic/Standard/Premium), subscribe / switch / cancel.
- **Admin tools** — add games, publish/hide them, delete them.

## Tech stack

| Layer     | Choice |
|-----------|--------|
| Backend   | Spring Boot 3.5, Java 17 |
| Data      | Spring Data JPA — MySQL (local), H2 (cloud/test profiles) |
| Security  | Spring Security, BCrypt, JJWT |
| UI        | Thymeleaf + Bootstrap 5 |
| Build/CI  | Maven, GitHub Actions (build + tests + Trivy scan), Docker |

## Project structure

```
com.gameflix.auth
├── controller   AuthController, ApiController, Home/Catalog/Subscription/Admin/Page
├── service      UserService, GameService, SubscriptionService
├── repository   UserRepository, GameRepository, SubscriptionRepository
├── model        UserAccount, Game, Subscription
├── security     JwtService, JwtAuthFilter
├── config       SecurityConfig
└── GameDataLoader   seeds games + a demo account on first run
```

## Running locally

Requires JDK 17 and a MySQL schema named `gameflix_db` (defaults are in
`application.properties`; override any of them with `SPRING_DATASOURCE_*` env vars).

```bash
mvn spring-boot:run
```

Then open <http://localhost:8080>. A demo account (`demo` / `demo123`) and sample
games are seeded automatically.

### Running without MySQL (cloud/H2 profile)

```bash
SPRING_PROFILES_ACTIVE=cloud mvn spring-boot:run
```

This uses an in-memory H2 database — nothing external to install. This is the
profile the deployed container runs.

## Key URLs

| URL | What it shows |
|-----|---------------|
| `/` | Dashboard |
| `/catalog` | Game catalog + search (`/catalog?q=hades`) |
| `/subscription` | Manage the demo user's plan |
| `/admin` | Manage the game library |
| `/login`, `/register` | Auth pages that call the JSON API and display the JWT |

## Secured API demo

```bash
# 1. Log in and grab the token
curl -s -X POST localhost:8080/login \
  -H "Content-Type: application/json" \
  -d '{"username":"demo","password":"demo123"}'

# 2. Call the protected route with it
curl -s localhost:8080/api/me -H "Authorization: Bearer <token>"

# 3. Without the token -> 401
curl -i localhost:8080/api/me
```

## Tests

```bash
mvn test
```

Runs against in-memory H2 (`test` profile): `GameServiceTest` (catalog/search
logic), `JwtServiceTest` (token round-trip + tampering), and a context-load smoke test.

See `PROMPT_JOURNAL.md` for how AI tooling was used, and `ATTACK_LOG.md` for the
security probing done against the auth/JWT layer.
