# WP-044 — Production environment

| Field | Value |
| --- | --- |
| **Phase** | 2 — Weeks 11–16 · Pilot-ready |
| **Status** | todo |
| **Depends on** | WP-007, WP-008, WP-009 |
| **Estimate** | TBD — expand when picking up this WP |

**Goal:** Provision `app-1` + `db-1` per §5 sizing with prod compose profile, MariaDB tuning, DNS cutover plan, blue/green procedure, and prod backups per §10.

**Context to load:**
- `docs/ghana-ehr-master-plan.md` §5 (sizing), §10

**Tasks:**
1. Provision `app-1` + `db-1` per §5 sizing (ADR if co-locating first).
2. Create the prod compose profile.
3. Tune MariaDB (buffer pool, slow-query log).
4. Write the DNS cutover plan.
5. Document the blue/green procedure.
6. Set up prod backups per §10.

**Out of scope:** TBD — expand when picking up this WP

**Acceptance criteria:**
- [ ] Prod serves the distro.
- [ ] Restore-to-prod drill passed.
- [ ] Tuning values recorded.

**Test plan:** TBD — expand when picking up this WP

**Artifacts:** TBD — expand when picking up this WP
