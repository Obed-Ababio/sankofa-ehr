# WP-073 — SMS reminders

| Field | Value |
| --- | --- |
| **Phase** | 4 — Months 7–9 · NHIS & growth |
| **Status** | todo |
| **Depends on** | WP-043 |
| **Estimate** | TBD — expand when picking up this WP |

**Goal:** Implement an SMS provider adapter behind the §8 interface (Hubtel or Africa's Talking, ADR-005) for appointment and immunization-due reminders with opt-in consent and STOP handling.

**Context to load:**
- `docs/ghana-ehr-master-plan.md` §8

**Tasks:**
1. Build the provider adapter (Hubtel or Africa's Talking — pick by pricing/API test, ADR-005) behind the §8 interface.
2. Implement appointment + immunization-due reminders.
3. Implement the opt-in consent flag + STOP handling.
4. Configure per-clinic sender.

**Out of scope:** TBD — expand when picking up this WP

**Acceptance criteria:**
- [ ] Sandbox + live test messages sent.
- [ ] Opt-out honored (test).
- [ ] Cost-per-message logged.

**Test plan:** TBD — expand when picking up this WP

**Artifacts:** TBD — expand when picking up this WP
