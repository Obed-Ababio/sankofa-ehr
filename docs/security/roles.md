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

## FHIR R4 privilege bypass — FIXED 2026-08-24 (fhir2 2.5.1)

Found 2026-08-22 during verification: fhir2 **2.0.0** (refapp-bundled) did not
enforce OpenMRS privileges — a Front Desk user denied `GET /ws/rest/v1/obs`
(403) still received clinical Observations from `/ws/fhir2/R4/Observation`.
This turned out to be a published vulnerability: **CVE-2025-46823 /
[GHSA-g5vq-w8v2-4x9j](https://github.com/openmrs/openmrs-module-fhir2/security/advisories/GHSA-g5vq-w8v2-4x9j)**
(critical) — fhir2 before 2.5.0 doesn't always check privileges.

Fix: the distro build replaces the bundled omod with **fhir2 2.5.1** (the
patched line for platform 2.4.1+; 3.x/4.x require platform ≥2.7) the same way
it swaps Initializer — see `distro/pom.xml`. Verified live: Front Desk is now
denied FHIR Observation/Encounter (`Privileges required: Get Observations`)
while keeping FHIR Patient lookup; Clinician retains full clinical reads.
`roles.spec.ts` pins the denial in CI so a base-distro bump can't silently
reintroduce the vulnerable version.

Known wart: 2.5.1 surfaces the denial as an OperationOutcome with HTTP 500
rather than 403. Harmless (no data leaks) but ugly — candidate upstream
report/PR alongside the CIEL ones.

## Open question: dedicated Nurse (Triage) role

Today a nurse who takes vitals logs in as Clinician — the only role with
clinical writes. That over-grants (order basket, full chart, diagnosis edit);
the only thing stopping a nurse from signing orders is the missing Provider
record, which crashes the workspace rather than denying cleanly. A fifth role
(vitals encounter + obs writes + queue management, no drug-order privileges)
is cheap to add with the computed-privilege approach used for Front Desk.
Blocked on clinical input, not engineering: ask the clinician reviewer whether
triage staff should see past visits and ever place orders.

## Users

No Initializer domain exists for users (credentials don't belong in git).
Dev/CI users come from `tools/create-dev-users.sh`; production users are
created per-clinic at go-live with real names and strong passwords, one
account per human (no shared logins — audit trail depends on it).
