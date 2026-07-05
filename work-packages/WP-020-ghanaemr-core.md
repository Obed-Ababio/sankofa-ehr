# WP-020 — ghanaemr-core scaffold

| Field | Value |
| --- | --- |
| **Phase** | 1 — Weeks 3–10 · Core distro |
| **Status** | todo |
| **Depends on** | WP-001 |
| **Estimate** | TBD — expand when picking up this WP |

**Goal:** Scaffold the `ghanaemr-core` Maven OMOD with activator, config-flags pattern, §8 adapter interfaces with mocks + contract tests, and CI wiring.

**Context to load:**
- `docs/ghana-ehr-master-plan.md` §8 (integrations & interoperability)
- §11

**Tasks:**
1. Create the Maven OMOD skeleton.
2. Add the activator.
3. Establish the config flags pattern.
4. Define adapter interfaces per §8 with mocks + contract tests.
5. Wire into CI.

**Out of scope:** TBD — expand when picking up this WP

**Acceptance criteria:**
- [ ] Module loads on the platform.
- [ ] Sample REST ping resource works.
- [ ] ≥80% coverage on service code from the start.

**Test plan:** TBD — expand when picking up this WP

**Artifacts:** TBD — expand when picking up this WP
