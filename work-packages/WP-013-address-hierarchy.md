# WP-013 — Ghana address hierarchy

| Field | Value |
| --- | --- |
| **Phase** | 1 — Weeks 3–10 · Core distro |
| **Status** | todo |
| **Depends on** | WP-010 |
| **Estimate** | TBD — expand when picking up this WP |

**Goal:** Load the Ghana regions/districts address hierarchy from Ghana Statistical Service data and wire the registration address fields.

**Context to load:**
- `docs/ghana-ehr-master-plan.md` §13.4 entry
- Ghana Statistical Service regions/districts data

**Tasks:**
1. Source the regions/districts CSV from Ghana Statistical Service (record source + date).
2. Build the addresshierarchy config.
3. Configure registration address fields.

**Out of scope:** TBD — expand when picking up this WP

**Acceptance criteria:**
- [ ] Cascading region → district selection works.
- [ ] Source cited.

**Test plan:** TBD — expand when picking up this WP

**Artifacts:** TBD — expand when picking up this WP
