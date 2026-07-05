# WP-083 — Keycloak SSO + MFA

| Field | Value |
| --- | --- |
| **Phase** | 5 — Months 10–14 · Scale & interop |
| **Status** | todo |
| **Depends on** | — |
| **Estimate** | TBD — expand when picking up this WP |

**Goal:** Migrate auth to Keycloak with TOTP MFA for admin/clinician roles, a fallback plan, and session policies.

**Context to load:**
- `docs/ghana-ehr-master-plan.md` §9

**Tasks:**
1. Migrate auth to Keycloak.
2. Enable TOTP MFA for admin/clinician roles.
3. Write the fallback plan.
4. Define session policies.

**Out of scope:** TBD — expand when picking up this WP

**Acceptance criteria:**
- [ ] MFA enforced for admins.
- [ ] Login e2e updated.
- [ ] Break-glass local account documented.

**Test plan:** TBD — expand when picking up this WP

**Artifacts:** TBD — expand when picking up this WP
