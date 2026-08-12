# Git & GitHub Guide — Uploading the Final GameFlix

Follow these steps in order. Run every command from inside the `gameflix-auth`
folder — that's the one that contains the `.git` folder, `pom.xml`, and `src`.

Open a terminal there. In IntelliJ you can use the built-in Terminal tab, which
already opens in the project folder.

> Throughout, replace `YOUR-USERNAME` and `YOUR-REPO` with your actual GitHub
> username and repository name.

---

## Step 1 — Confirm where you are

```bash
git status
```

If it prints a list of changes, you're in the right folder. If it says "not a git
repository," you're in the wrong folder — `cd` into `gameflix-auth` and try again.

---

## Step 2 — Get the latest from GitHub first

This avoids conflicts if anything changed online.

```bash
git pull origin main
```

If it complains that you have local changes, that's fine — the next steps commit
them. If it opens a text editor asking for a merge message, just save and close it.

---

## Step 3 — Stop tracking files that shouldn't be in git

A `.gitignore` is already included, but two folders were probably committed in an
earlier version and git keeps tracking them until you tell it to stop:

- `target/` — the build output, regenerated every build
- `.idea/` — IntelliJ's personal settings

Remove them from git **without deleting them from your disk**:

```bash
git rm -r --cached target .idea
```

If it says `pathspec 'target' did not match` for one of them, that folder simply
wasn't tracked — ignore the message and continue.

---

## Step 4 — Stage your source changes

Stage everything that should be in the repo (source, templates, config, docs). The
`.gitignore` automatically keeps `target/` and `.idea/` out.

```bash
git add .
```

Double-check what's about to be committed:

```bash
git status
```

You want to see your Java files, `src/main/resources/templates/*.html`,
`pom.xml`, `Dockerfile`, `.github/workflows/ci.yml`, and the markdown docs. You do
**not** want to see anything under `target/`. If you do, redo Step 3.

---

## Step 5 — Commit

Commit in a couple of logical chunks so the history is readable. Because this
release was heavily AI-assisted, tag those commits with `[AI-assisted]`.

```bash
git commit -m "Realign domain to GameFlix: UserAccount, Game, Subscription [AI-assisted]"
```

If you'd rather do it all at once, a single commit is fine too:

```bash
git commit -m "Rebuild as GameFlix: domain, JWT API, UI, tests, CI, docs [AI-assisted]"
```

Example messages for other typical changes:

- `git commit -m "Add JWT-secured /api/me route and auth filter [AI-assisted]"`
- `git commit -m "Add Thymeleaf catalog, subscription, and admin pages [AI-assisted]"`
- `git commit -m "Update CI to run tests on H2 and add Trivy scan [AI-assisted]"`
- `git commit -m "Fix subscribe() to update existing plan instead of duplicating"`  *(your own fix — no tag needed)*

---

## Step 6 — Push to GitHub

```bash
git push origin main
```

If this is a brand-new repo and git says there's no upstream, run this once instead:

```bash
git push -u origin main
```

If you don't have a remote yet:

```bash
git remote add origin https://github.com/YOUR-USERNAME/YOUR-REPO.git
git push -u origin main
```

---

## Step 7 — Verify on the GitHub website

1. Open `https://github.com/YOUR-USERNAME/YOUR-REPO` in your browser.
2. Confirm the file list shows `src/`, `pom.xml`, `Dockerfile`, `README.md`,
   `PROMPT_JOURNAL.md`, `REFLECTION.md`, `ATTACK_LOG.md`, and the others.
3. Confirm there is **no** `target/` folder and **no** `.idea/` folder.
4. Click into a couple of files (e.g. `GameService.java`, `PROMPT_JOURNAL.md`) to
   confirm they show your latest content.

---

## Step 8 — Check the CI pipeline

1. On the repo page, click the **Actions** tab.
2. You'll see a run named **GameFlix CI** for your push. Click it.
3. Wait for it to finish. A green check means: Maven built, the tests passed, the
   Trivy scan ran, and the Docker image built.

**If CI fails, click the red step to read the log. Common causes:**

- **A test failed.** Read which test. Reproduce locally with `mvn test` and fix.
- **`package` failed to compile.** The log names the file and line. Fix and push again.
- **Docker build failed.** Usually a syntax slip in the `Dockerfile`; the log shows
  the failing line.
- **Trivy step red.** By default it's set to report only (`exit-code: 0`) and won't
  fail the build. If you changed it to `1`, either update the vulnerable dependency
  or set it back to `0`.

After any fix: `git add .` → `git commit -m "Fix CI: <what> [AI-assisted]"` →
`git push origin main`, and watch Actions again.

---

## Step 9 — Deploy (Render, using Docker + the H2 cloud profile)

Render is the default here because it's free, gives you a public URL, and builds
straight from your `Dockerfile`. The app's `cloud` profile uses an in-memory H2
database, so you don't have to provision a separate MySQL server just to demo it.

1. Go to <https://render.com>, sign up, and connect your GitHub account.
2. Click **New → Web Service** and pick your `YOUR-REPO` repository.
3. Render detects the `Dockerfile`. Set:
   - **Environment:** Docker
   - **Region / Instance type:** Free
   - **Environment variables** (Advanced → Add):
     - `SPRING_PROFILES_ACTIVE` = `cloud`
     - `JWT_SECRET` = any long random string (32+ characters)
4. Click **Create Web Service**. Render builds the image (the multi-stage Dockerfile
   compiles the app inside the build) and starts it.
5. When the log shows `Started GameflixAuthApplication`, your app is live.
6. The public URL is at the top of the service page, like
   `https://gameflix-YOUR-REPO.onrender.com`. Open it — you should see the dashboard,
   and the demo account is seeded automatically.

> **Alternative — Railway** (<https://railway.app>): also deploys from the Dockerfile.
> If you'd rather use a real MySQL there, add a MySQL plugin and set
> `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, and
> `SPRING_DATASOURCE_PASSWORD` from the plugin's connection details instead of
> setting `SPRING_PROFILES_ACTIVE=cloud`.

> **Note:** free Render services sleep after inactivity and take ~30 seconds to wake.
> Open the URL a minute before you start recording your demo so it's warm.

---

## Step 10 — What to submit to the course

| Artifact | Where it is |
|----------|-------------|
| GitHub repo URL | `https://github.com/YOUR-USERNAME/YOUR-REPO` |
| Deployed app URL | Your Render URL, e.g. `https://gameflix-YOUR-REPO.onrender.com` |
| Prompt Journal | `PROMPT_JOURNAL.md` in the repo |
| Reflection essay | `REFLECTION.md` in the repo |
| Attack log | `ATTACK_LOG.md` in the repo |
| 10-minute demo video | Record with the deployed app + `DEMO_SCRIPT.md`, upload, paste the link |

Do a final check that the repo is either public or that your instructor has access,
and that the deployed URL loads in a fresh browser tab before you submit.
