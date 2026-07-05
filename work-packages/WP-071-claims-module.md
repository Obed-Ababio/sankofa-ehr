# WP-071 — Claims module

| Field | Value |
| --- | --- |
| **Phase** | 4 — Months 7–9 · NHIS & growth |
| **Status** | todo |
| **Depends on** | WP-070 |
| **Estimate** | TBD — expand when picking up this WP |

**Goal:** Build `ghanaemr-claims` OMOD + `esm-ghana-claims`: NHIS eligibility capture, visit→claim assembly, validation queue UI, batch export per ADR-004, and manual status tracking.

**Context to load:**
- `docs/ghana-ehr-master-plan.md` §8
- ADR-004 (from WP-070)

**Tasks:**
1. Build the `ghanaemr-claims` OMOD + `esm-ghana-claims`.
2. Implement NHIS eligibility capture at check-in.
3. Implement visit → claim assembly (ICD-10 → G-DRG mapping table as maintained CSV, NHIS-list medicines pricing).
4. Build the validation queue UI.
5. Implement batch export per ADR-004.
6. Implement manual status tracking (submitted/paid/rejected + reason).

**Out of scope:** TBD — expand when picking up this WP

**Acceptance criteria:**
- [ ] End-to-end claim for 5 visit archetypes matches hand-computed expected values.
- [ ] Rejection-reason report exists.

**Test plan:** TBD — expand when picking up this WP

**Artifacts:** TBD — expand when picking up this WP
