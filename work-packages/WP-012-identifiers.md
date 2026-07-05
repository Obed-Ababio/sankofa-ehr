# WP-012 — Identifier types & Ghana Card validation

| Field | Value |
| --- | --- |
| **Phase** | 1 — Weeks 3–10 · Core distro |
| **Status** | todo |
| **Depends on** | WP-010, WP-011, WP-020 |
| **Estimate** | M |

**Goal:** All four identifiers from §6.1 exist as metadata; Network MRN auto-generates; Ghana Card numbers are format-validated at registration.

**Context to load:**
- `docs/ghana-ehr-master-plan.md` §6.1
- `distro/configs/` Initializer docs (patientidentifiertypes, idgen domains — read the Initializer README for exact CSV headers, do not guess)
- `backend/ghanaemr-core`

**Tasks:**
1. Initializer CSVs for the 4 identifier types (Ghana Card, NHIS, phone-as-attribute per O3 registration convention — check O3 registration config docs for attribute vs identifier handling of phone).
2. idgen source for MRN `SNK-` prefix, 6 digits, mod-30 check, autogeneration on registration.
3. In `ghanaemr-core`, a `PatientIdentifierValidator`-conforming Ghana Card validator: regex `^GHA-\d{9}-\d$`; checksum verification stubbed behind config flag `ghanaemr.ghanacard.checksum=off` with TODO referencing NIA spec acquisition.
4. Wire validator to the identifier type.
5. Registration form config: Ghana Card + NHIS + phone fields, MRN read-only.
6. Unit tests: 10 valid/invalid PIN cases; idgen check-digit tests.
7. Update `docs/identifiers.md`.

**Out of scope:** NIA online verification (adapter mocked per §8); duplicate-merge tooling (WP-075).

**Acceptance criteria:**
- [ ] Registering without Ghana Card succeeds (optional field).
- [ ] Malformed PIN blocks save with clear message.
- [ ] MRN auto-assigned and unique across two test clinics.
- [ ] All metadata reproducible on a clean `compose up` (CI job proves it).
- [ ] Unit tests green.

**Test plan:** JUnit validator tests; Playwright registration test (valid + invalid PIN); CI clean-environment metadata-load job.

**Artifacts:** `distro/configs/{patientidentifiertypes,idgen,attributetypes}/…`, `backend/ghanaemr-core/src/...Validator.java`, `docs/identifiers.md`.
