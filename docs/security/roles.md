# Roles & privilege matrix

*Task 1.6. Source of truth: `configuration/roles/roles.csv`. Verified by
`e2e/tests/roles.spec.ts` (Front Desk denied clinical reads at the API level —
Test Gate 1 item 6).*

## Why not the stock roles

OpenMRS's reference roles (e.g. *Organizational: Registration Clerk*) all
inherit **Privilege Level: High** — 210 privileges including reading
encounters, observations and orders. Role separation there is cosmetic. Our
roles are built from explicit privilege lists instead.

## The four roles

| Role | Who | Can | Cannot |
|---|---|---|---|
| **Sankofa: Front Desk** | Receptionist/records clerk | Log in, register patients, search by any identifier/phone/name, start visits & queues | Read or write ANY clinical data: encounters, observations, diagnoses, conditions, orders, allergies, programs (privileges excluded outright, not hidden in UI); no metadata admin |
| **Sankofa: Clinician** | Doctors, PAs, nurses doing consultations | Everything clinical: chart, vitals, notes, allergies, ADT (inherits the stock Application clinical roles) | System administration |
| **Sankofa: Clinic Admin** | Practice manager | System config, forms, metadata, user management | — (but metadata changes belong in `configuration/`, not the admin UI — see conventions) |
| **Sankofa: Support (Read-only)** | Remote support engineers | View everything | Change anything (all Add/Edit/Delete/Manage/Purge privileges excluded) |

## Mechanics

- Front Desk = Privilege Level High **minus** clinical-data privileges (any
  data-level privilege on Allergies/Conditions/Diagnoses/Encounters/
  Observations/Orders/Patient Programs, plus the patient-dashboard clinical
  widgets) **minus** all `Manage *` privileges. 132 privileges remain —
  enough to run the O3 SPA, registration and search.
- Support = High minus every write verb (Add/Edit/Delete/Remove/Purge/Manage).
- Metadata-type reads (e.g. *Get Encounter Types*) stay in Front Desk — they
  are schema, not patient data, and the SPA needs them to boot.

## Known gap: FHIR R4 bypasses privileges (tracked for Stage 3)

Found 2026-08-22 during verification: the **fhir2 module does not enforce
OpenMRS privileges** — a Front Desk user denied `GET /ws/rest/v1/obs` (403)
still receives clinical Observations from `/ws/fhir2/R4/Observation`
(confirmed empirically with a seeded vitals encounter). The O3 REST endpoints
the clinical widgets use ARE enforced; the leak is the FHIR surface.

- Marked in the e2e suite as a `test.fixme` in `roles.spec.ts` so it stays visible.
- **Stage 3 hardening item:** evaluate fhir2 upgrade (newer versions add
  `@Authorized` checks) → else proxy rule / upstream PR (our principle:
  upstream fixes are PR'd upstream). Decision goes in an ADR.
- Until then: FHIR is treated as a trusted/system boundary; clinic staff
  accounts are trusted-but-audited per the pilot contract.

## Users

No Initializer domain exists for users (credentials don't belong in git).
Dev/CI users come from `tools/create-dev-users.sh`; production users are
created per-clinic at go-live with real names and strong passwords, one
account per human (no shared logins — audit trail depends on it).
