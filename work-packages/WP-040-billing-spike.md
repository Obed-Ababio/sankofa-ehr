# WP-040 — Billing decision spike

| Field | Value |
| --- | --- |
| **Phase** | 2 — Weeks 11–16 · Pilot-ready |
| **Status** | todo |
| **Depends on** | WP-010 |
| **Estimate** | TBD — expand when picking up this WP |

**Goal:** Evaluate the community billing module + esm-billing against our platform pins on a branch (timeboxed 3 days) and record the chosen path in ADR-002.

**Context to load:**
- `docs/ghana-ehr-master-plan.md` §13.5 entry
- Community billing module + esm-billing docs

**Tasks:**
1. Evaluate the community billing module + esm-billing against our platform pins on a branch; timebox 3 days.
2. Write ADR-002 choosing path A (adopt + configure) or path B (minimal `ghanaemr-billing`: tables `price_list_item(clinic, item_type[svc|drug|lab], concept/drug ref, price, active)`, `invoice(visit, status)`, `invoice_line`, `payment(method[cash|momo], amount, ref)`; auto-line-items from orders/dispensing; void-with-reason).

**Out of scope:** TBD — expand when picking up this WP

**Acceptance criteria:**
- [ ] ADR-002 signed.
- [ ] Chosen path's schema/config documented.

**Test plan:** TBD — expand when picking up this WP

**Artifacts:** TBD — expand when picking up this WP
