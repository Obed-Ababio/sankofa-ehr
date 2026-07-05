# WP-041 — Billing implementation

| Field | Value |
| --- | --- |
| **Phase** | 2 — Weeks 11–16 · Pilot-ready |
| **Status** | todo |
| **Depends on** | WP-040 |
| **Estimate** | TBD — expand when picking up this WP |

**Goal:** Implement the billing path chosen in ADR-002: price lists, cashier screen, invoicing, cash/MoMo payments, receipts, voids, and daily cash summary.

**Context to load:**
- `docs/ghana-ehr-master-plan.md` §6.4
- ADR-002 (from WP-040)

**Tasks:**
1. Implement per-clinic price lists (CSV via Initializer or import script).
2. Build the cashier screen.
3. Generate invoice from visit.
4. Record cash/MoMo payments.
5. Implement receipt print.
6. Implement the void flow.
7. Build the daily cash summary feeding WP-030.

**Out of scope:** TBD — expand when picking up this WP

**Acceptance criteria:**
- [ ] Full billed-visit e2e passes.
- [ ] Cash summary reconciles to the payments table exactly.
- [ ] Price update procedure documented.

**Test plan:** TBD — expand when picking up this WP

**Artifacts:** TBD — expand when picking up this WP
