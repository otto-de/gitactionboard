# Product

<!-- impeccable:product-schema 1 -->

## Platform

web

## Users

Primary: developers and teams who need GitHub Actions CI health for multiple repos in one place instead of checking each repo's Actions tab separately. Two usage patterns already coexist in the shipped app via the `enableBuildMonitorView` preference: an actively-checked "individual" view (roomier cards, for someone at their own desk) and a denser "build monitor" grid (built for glancing at from a distance, e.g. a wallboard). [Inferred from the existing density toggle in `Dashboard.vue`/`GridCell.vue`; the user did not confirm the exact split between these two usage modes and pointed instead to `doc/mockups/option1-linear-clean.html` as a reference — that mockup explores naming them "Personal" vs "Build Monitor" modes, which is exploratory design direction, not confirmed product terminology.]

Secondary: users who also care about repo security posture — the same board can surface GitHub secret-scan and code-scan alerts (opt-in via env flags), for whoever owns that governance.

## Product Purpose

A self-hosted, single dashboard that aggregates GitHub Actions workflow run status across multiple repos (and an org), so "is anything red right now" is answerable at a glance, with drill-down into the specific failing run. Extended with GitHub secret-scan and code-scan alert visibility and MS Teams failure notifications, so CI health and basic security signal live in one place. Success is a viewer correctly and quickly telling build/security health apart without opening GitHub.

## Positioning

Two things a bespoke internal script or GitHub's own UI wouldn't give you, both load-bearing per the README's feature list: (1) it speaks the open CCTray XML/JSON protocol, so it plugs into pre-existing build-radiator/CI-monitoring tooling instead of locking a team into its own dashboard; (2) it unifies build status with secret-scan and code-scan alerts on one board instead of leaving security signal in a separate tool. [Inferred from repo evidence (README feature list, USAGE.md API docs) — the user was asked to pick one as primary and instead deferred, saying they're scoped to a UI revamp of the existing product, not a positioning decision this session.]

## Operating Context

- Self-hosted via Docker; configured through environment variables (repo owner/names, GitHub token, OAuth2 client id/secret, MS Teams webhook URL, cache TTL, periodic scan cron schedule).
- Gated by Basic Auth (htpasswd file) or GitHub OAuth2 login.
- Polls GitHub for status on an interval; pauses polling after a configurable idle time to avoid unnecessary API calls, and caches responses (default 60s) to respect GitHub API rate limits.
- Dark and light themes are both first-class and documented (`doc/dark-theme/`, `doc/light-theme/` screenshots) — theme parity across every surface is an existing commitment, not optional.
- Frontend is Vue 3 + Vuetify + Vite, tested with Vitest, linted with ESLint/Stylelint (existing stack; not a decision made this session, so no `## Stack` section is recorded).

## Capabilities and Constraints

- Aggregates GitHub Actions workflow run status across one or more repos under one owner/org.
- Exposes the same run data as CCTray-compatible XML and JSON (`/v1/cctray`, `/v1/cctray.xml`) — this API contract is a durable constraint that any UI change must not break.
- Optional, env-gated GitHub secret-scan alert and code-scan alert monitoring, each with its own JSON API.
- Sends MS Teams failure notifications via incoming webhook or workflow URL.
- Filters: hide healthy/idle builds; filter by branch name and triggered event type.
- Key metrics for CI reliability/performance (charts, via Chart.js).
- Preferences page manages dashboard configuration from v2.0.0 onward; older query-param configuration (`hide-healthy`, `max-idle-time`, `disable-max-idle-time`) is preserved for back-compat.

## Brand Commitments

- Name "GitactionBoard" (repo/package name) / "Gitaction Board" (README title) and its favicon are the only fixed identity assets on hand; no other logo, wordmark, or brand guideline exists in the repo.
- Open-source project under the `otto-de` GitHub org, OSS lifecycle "active." No evidence it must carry otto's retail brand — treat as an independent OSS identity unless told otherwise.

## Evidence on Hand

- "Before" reference screenshots for every current surface in both themes: `doc/dark-theme/*.png` and `doc/light-theme/*.png` (login, workflow, metrics, preferences, code-standard-violations, exposed-secrets).
- An exploratory visual-direction mockup already exists at `doc/mockups/option1-linear-clean.html`/`.png` (a "Linear-clean" direction: dark/light tokens, a "Personal" vs "Build Monitor" mode framing, a preferences card). This is untracked, exploratory work-in-progress, not an approved or shipped direction — a candidate reference for a later new-work/DESIGN.md pass, not confirmed product truth.
- An untracked `plan.md` already lays out a phased UI-revamp approach (audit → design direction → per-surface revamp → harden → verify) targeting the same surfaces.
- No customer testimonials, logos, or case studies exist in the repo — none should be fabricated.

## Product Principles

- One glanceable board beats N separate CI tabs — protect at-a-glance pass/fail scanability first.
- Stay protocol-open: don't trade away the CCTray API contract for a prettier UI.
- Security signal (secrets/code-scan alerts) is a first-class citizen of the board, not an afterthought.
- Dark/light parity is non-negotiable — every surface change ships in both themes.
- Preserve the existing configuration surface (env vars, query params, preferences) unless a change is explicitly requested.

## Accessibility & Inclusion

No project-specific accessibility requirement was established in this session; general web accessibility best practice applies by default, not a confirmed standard.
