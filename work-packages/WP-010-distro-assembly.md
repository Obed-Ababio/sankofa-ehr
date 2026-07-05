# WP-010 — Distro assembly

| Field | Value |
| --- | --- |
| **Phase** | 1 — Weeks 3–10 · Core distro |
| **Status** | todo |
| **Depends on** | WP-001 |
| **Estimate** | TBD — expand when picking up this WP |

**Goal:** Fork/derive the reference-application assembly into our own distro with the §5 backend module set, pinned SPA build, and empty `distro/configs` wiring.

**Context to load:**
- `docs/ghana-ehr-master-plan.md` §5, §11
- `VERSIONS.md`

**Tasks:**
1. Fork/derive the reference-application assembly.
2. Assemble the backend module set per §5 (excluding Phase-2/4 modules).
3. Build the SPA with pinned ESMs.
4. Wire empty `distro/configs`.
5. Produce dev and staging images.

**Out of scope:** TBD — expand when picking up this WP

**Acceptance criteria:**
- [ ] Our distro (not stock) runs on staging.
- [ ] Module list matches `VERSIONS.md`.

**Test plan:** TBD — expand when picking up this WP

**Artifacts:** TBD — expand when picking up this WP
