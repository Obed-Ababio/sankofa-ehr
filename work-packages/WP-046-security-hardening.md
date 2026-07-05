# WP-046 — Security hardening pass

| Field | Value |
| --- | --- |
| **Phase** | 2 — Weeks 11–16 · Pilot-ready |
| **Status** | todo |
| **Depends on** | WP-020, WP-044 |
| **Estimate** | TBD — expand when picking up this WP |

**Goal:** Implement the §9 technical-controls list, including the audit-approach decision (ADR-003), password/lockout policy, tailnet-only superuser, Trivy gates, OWASP headers, and Cloudflare rate rules.

**Context to load:**
- `docs/ghana-ehr-master-plan.md` §9

**Tasks:**
1. Implement the §9 technical-controls list.
2. Decide and implement the audit approach (ADR-003).
3. Set the password/lockout policy.
4. Enforce proxy-level tailnet-only superuser access.
5. Add Trivy gates.
6. Add OWASP headers.
7. Configure Cloudflare rate rules.

**Out of scope:** TBD — expand when picking up this WP

**Acceptance criteria:**
- [ ] Documented checklist 100% complete.
- [ ] Audit rows verified for restricted reads + all mapping changes.
- [ ] Scanner shows zero criticals.

**Test plan:** TBD — expand when picking up this WP

**Artifacts:** TBD — expand when picking up this WP
