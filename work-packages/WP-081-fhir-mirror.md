# WP-081 — FHIR mirror (SHR-lite)

| Field | Value |
| --- | --- |
| **Phase** | 5 — Months 10–14 · Scale & interop |
| **Status** | todo |
| **Depends on** | WP-020 |
| **Estimate** | TBD — expand when picking up this WP |

**Goal:** Stand up a HAPI FHIR JPA service (Postgres 16) synced incrementally from fhir2, with §8 resources, an IPS summary endpoint, OAuth2 client-credentials, and isolation invariants re-proven at the mirror.

**Context to load:**
- `docs/ghana-ehr-master-plan.md` §8

**Tasks:**
1. Stand up the HAPI FHIR JPA (Postgres 16) service.
2. Implement sync from fhir2 (incremental, idempotent, monitored).
3. Expose resources per §8.
4. Implement the IPS summary endpoint.
5. Implement OAuth2 client-credentials.
6. Re-prove isolation invariants at the mirror.

**Out of scope:** TBD — expand when picking up this WP

**Acceptance criteria:**
- [ ] External test client retrieves IPS for a consented patient only.
- [ ] Sync lag alarmed.
- [ ] Conformance statement published.

**Test plan:** TBD — expand when picking up this WP

**Artifacts:** TBD — expand when picking up this WP
