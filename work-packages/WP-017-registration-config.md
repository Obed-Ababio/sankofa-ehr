# WP-017 — Registration config

| Field | Value |
| --- | --- |
| **Phase** | 1 — Weeks 3–10 · Core distro |
| **Status** | todo |
| **Depends on** | WP-012, WP-013 |
| **Estimate** | TBD — expand when picking up this WP |

**Goal:** Configure registration with the §6.1/§6.3 field set, search-before-create, and an exact-Ghana-Card duplicate block.

**Context to load:**
- `docs/ghana-ehr-master-plan.md` §6.1, §6.3

**Tasks:**
1. Configure the field set per §6.1/§6.3.
2. Implement search-before-create (GhanaCard → phone → fuzzy).
3. Implement the exact-GhanaCard duplicate block.

**Out of scope:** TBD — expand when picking up this WP

**Acceptance criteria:**
- [ ] Playwright covers create, duplicate-block, and search paths.

**Test plan:** TBD — expand when picking up this WP

**Artifacts:** TBD — expand when picking up this WP
