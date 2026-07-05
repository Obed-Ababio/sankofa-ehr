# WP-047a — Staging anonymizer

| Field | Value |
| --- | --- |
| **Phase** | 2 — Weeks 11–16 · Pilot-ready |
| **Status** | todo |
| **Depends on** | — |
| **Estimate** | TBD — expand when picking up this WP |

**Goal:** Build a deterministic scrubber (names→synthetic, contacts→fake, GhanaCard→invalidated pattern) for prod→staging refreshes, with verification queries proving no real PII.

**Context to load:**
- `docs/ghana-ehr-master-plan.md` §9, §10

**Tasks:**
1. Build the deterministic scrubber: names → synthetic, contacts → fake, GhanaCard → invalidated pattern, for prod → staging refreshes.
2. Write verification queries that prove no real PII remains.

**Out of scope:** TBD — expand when picking up this WP

**Acceptance criteria:**
- [ ] Anonymizer test suite green.
- [ ] Verification queries prove no real PII.

**Test plan:** TBD — expand when picking up this WP

**Artifacts:** TBD — expand when picking up this WP
