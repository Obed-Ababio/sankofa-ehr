# WP-074 — Analytics

| Field | Value |
| --- | --- |
| **Phase** | 4 — Months 7–9 · NHIS & growth |
| **Status** | todo |
| **Depends on** | WP-044 |
| **Estimate** | TBD — expand when picking up this WP |

**Goal:** Stand up a MariaDB read replica and Metabase over curated SQL views, with per-clinic collections mirroring Data Filter scoping.

**Context to load:**
- `docs/ghana-ehr-master-plan.md` §13.7 entry

**Tasks:**
1. Set up the MariaDB read replica.
2. Stand up Metabase over curated SQL views (visits, revenue, morbidity, stock, claims).
3. Create per-clinic collections with permissions mirroring Data Filter scoping.

**Out of scope:** TBD — expand when picking up this WP

**Acceptance criteria:**
- [ ] Owner dashboard exists for each pilot clinic.
- [ ] Replica lag alarmed.

**Test plan:** TBD — expand when picking up this WP

**Artifacts:** TBD — expand when picking up this WP
