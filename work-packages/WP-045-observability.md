# WP-045 — Observability

| Field | Value |
| --- | --- |
| **Phase** | 2 — Weeks 11–16 · Pilot-ready |
| **Status** | todo |
| **Depends on** | WP-044 |
| **Estimate** | TBD — expand when picking up this WP |

**Goal:** Wire Grafana Cloud agents, dashboards, alert rules with phone routing, an Uptime Kuma public page, and Sentry with release tags — with a runbook per alert.

**Context to load:**
- `docs/ghana-ehr-master-plan.md` §10

**Tasks:**
1. Install Grafana Cloud agents (host/JVM/MariaDB/HTTP).
2. Build dashboards.
3. Define alert rules + phone routing.
4. Stand up the Uptime Kuma public page.
5. Wire Sentry with release tags.
6. Write a runbook per alert.

**Out of scope:** TBD — expand when picking up this WP

**Acceptance criteria:**
- [ ] Synthetic failure of each alert fires within SLA.
- [ ] Runbooks linked from alerts.

**Test plan:** TBD — expand when picking up this WP

**Artifacts:** TBD — expand when picking up this WP
