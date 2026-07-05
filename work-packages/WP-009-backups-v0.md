# WP-009 — Backups v0

| Field | Value |
| --- | --- |
| **Phase** | 0 — Weeks 1–2 |
| **Status** | todo |
| **Depends on** | WP-007 |
| **Estimate** | TBD — expand when picking up this WP |

**Goal:** Nightly mariabackup plus binlog shipping to S3 (encrypted), a restore script, and a first logged restore drill.

**Context to load:**
- `docs/ghana-ehr-master-plan.md` §10

**Tasks:**
1. Set up mariabackup nightly.
2. Ship binlogs to S3 (encrypted).
3. Write the restore script.
4. Run and log the first restore drill.

**Out of scope:** TBD — expand when picking up this WP

**Acceptance criteria:**
- [ ] Drill restores staging to a ≤15-minute-old state on a scratch container.

**Test plan:** TBD — expand when picking up this WP

**Artifacts:** TBD — expand when picking up this WP
