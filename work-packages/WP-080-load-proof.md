# WP-080 — 50-clinic load proof

| Field | Value |
| --- | --- |
| **Phase** | 5 — Months 10–14 · Scale & interop |
| **Status** | todo |
| **Depends on** | WP-034 |
| **Estimate** | TBD — expand when picking up this WP |

**Goal:** Prove 50-clinic headroom: k6 profile per §4.3, find breakpoints, tune, and document verified headroom + sharding trigger metrics in ADR-006.

**Context to load:**
- `docs/ghana-ehr-master-plan.md` §4.3 (scale model & headroom)

**Tasks:**
1. Build the k6 profile per §4.3.
2. Find breakpoints.
3. Tune (pool sizes, heap, MariaDB).
4. Write ADR-006 documenting verified headroom + the sharding trigger metrics.

**Out of scope:** TBD — expand when picking up this WP

**Acceptance criteria:**
- [ ] Targets met at 50-clinic simulation on a prod-sized replica environment.

**Test plan:** TBD — expand when picking up this WP

**Artifacts:** TBD — expand when picking up this WP
