# WP-001 — Version pinning & stock spin-up

| Field | Value |
| --- | --- |
| **Phase** | 0 — Weeks 1–2 |
| **Status** | review |
| **Depends on** | WP-000 |
| **Estimate** | TBD — expand when picking up this WP |

**Goal:** Pin every platform, frontend, module, and runtime version from official OpenMRS sources into `VERSIONS.md` and stand up the unmodified reference-application distro locally via Docker.

**Context to load:**
- `docs/ghana-ehr-master-plan.md` §5 (technology stack)
- Official OpenMRS release sources (Platform, O3 frontend, modules)

**Tasks:**
1. From official OpenMRS sources, pin the Platform 2.7.x patch version.
2. Pin the O3 frontend release tags.
3. Pin all §5 module versions.
4. Pin Java / Tomcat / MariaDB versions.
5. Produce `VERSIONS.md` with a source link for every pinned version.
6. Stand up the unmodified reference-application distro locally via Docker.

**Out of scope:** TBD — expand when picking up this WP

**Acceptance criteria:**
- [x] `compose up` → login works. *(verified 2026-07-05, log below)*
- [x] Every version in `VERSIONS.md` cites a URL.
- [x] NO version numbers invented.

**Test plan:** manual verification of login on the stock stack (logged below); source-URL spot checks in `docs/VERSIONS.md` verification log.

**Artifacts:** `docs/VERSIONS.md`, `docs/adr/ADR-010-platform-2.7-vs-refapp-2.8.md`.

---

## Progress log

**2026-07-05** — `docs/VERSIONS.md` written; every pin verified against a fetched URL (GitHub API, OpenMRS Maven repo, Docker Hub, mariadb.org, nodejs.org). Nothing left unverified.

Findings needing attention:
1. **D1 conflict → ADR-010 (proposed, needs human sign-off):** RefApp 3.7.0 (latest stable) builds against core **2.8.7**, not 2.7.x as D1 states. Latest 2.7.x is 2.7.9. See `docs/adr/ADR-010-platform-2.7-vs-refapp-2.8.md`.
2. Several GitHub `releases/latest` entries are stale (openmrs-core, bahmni appointments, billing) — Maven metadata used as authoritative cross-check.
3. stockmanagement repo moved from METS-Programme to the openmrs org (old URL 404s).
4. RefApp compose pins `mariadb:10.11.7`; we pin latest LTS patch 10.11.18 for our own assembly (WP-010).
5. Local dev machine has Node v21.1.0 (non-LTS); LTS is v24.x — upgrade in WP-002.

Stock spin-up: RefApp cloned at tag `3.7.0`, `TAG=3.7.0 docker compose up -d` on local Docker (unmodified stock stack per AC). **Login verified**: `GET /openmrs/ws/rest/v1/session` with `admin/Admin123` returned `"authenticated":true` (Super User); SPA login page served throughout. Boot observations worth keeping:
- First boot took **~73 minutes** on a WSL2 dev machine — dominated by the stock demo Open Concept Lab (CIEL) import running as the initial-setup daemon task; `/openmrs/health/started` stays 503 (and REST 302s to `/initialsetup`) until it finishes, even while the Docker healthcheck reports "healthy". Poll `/health/started`, not the container status.
- The OCL demo import logged ~18 `SavingException` errors ("concept has not been imported" for some CIEL mappings) — stock demo-data noise, non-fatal. Not relevant to our distro: WP-014 loads CIEL from a packaged release, not OCL demo import.
- `make dev` (WP-002) wraps exactly this stack; the quickstart's "5–15 min" estimate should be read as "up to ~75 min on modest hardware for the very first boot".
