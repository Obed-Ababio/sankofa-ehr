# WP-022 — Cross-clinic network search + consent flow

| Field | Value |
| --- | --- |
| **Phase** | 1 — Weeks 3–10 · Core distro |
| **Status** | todo |
| **Depends on** | WP-017, WP-020, WP-021 |
| **Estimate** | L (the most sensitive WP in the project — human review of the diff is mandatory) |

**Goal:** Implement §7 exactly: privacy-preserving network patient search, consent capture, Data Filter mapping grant, break-the-glass, audit.

**Context to load:**
- `docs/ghana-ehr-master-plan.md` §7
- Data Filter module docs/README (entity-basis mapping API)
- O3 extension-slot docs for adding actions to patient-search UI
- `esm-ghana-patient-search` scaffold

**Tasks:**
1. `ghanaemr-core`: REST resource `GET /ws/rest/v1/ghanaemr/networksearch?q=` returning limited demographics (name, age band, sex, masked phone last-3, MRN last-3) for patients *not* mapped to caller's clinic; requires new privilege `App: ghanaemr.networkSearch`.
2. REST resource `POST /ghanaemr/accessgrant` {patient, clinic, mode: consent|emergency, reason?, consentEncounterUuid} → creates Data Filter mapping via module API + audit row (custom table `ghanaemr_access_audit`: actor, patient, clinic, mode, reason, timestamp — Liquibase changeset).
3. Consent encounter type + mini-form (attestation checkbox, optional consent-photo attachment).
4. `esm-ghana-patient-search`: "Search network" action on empty local results → results list → consent modal → grant → route to patient chart. Emergency path requires reason text, shows warning banner on chart for that session.
5. Weekly break-the-glass report added to WP-030 report set.
6. Contract tests: the two-user isolation suite (WP-021) extended — Clinic B user cannot read Clinic A patient via REST/FHIR before grant, can after, cannot after revoke; network search never returns clinical fields (schema-asserted).
7. Docs: `docs/consent-model.md` + training snippet.

**Out of scope:** Patient-initiated revocation UI (admin-mediated per §7); biometric identity.

**Acceptance criteria:**
- [ ] All §7 privacy invariants pass as automated tests.
- [ ] Grant/revoke round-trip works in UI.
- [ ] Every grant produces an audit row.
- [ ] Emergency grants appear in weekly report.
- [ ] No clinical data in network-search payload (test-asserted).

**Test plan:** JUnit service tests; REST contract suite (two clinics, three users); Playwright consent-flow e2e; FHIR isolation probe.

**Artifacts:** `backend/ghanaemr-core/...`, `frontend-esms/esm-ghana-patient-search/`, `distro/configs/encountertypes`, `docs/consent-model.md`.
