# Progress tracker

Status of the [master plan](ghana-clinic-emr-implementation-plan.md), updated at the end of every working session. Newest session first. This file is the "pick up where I left off" entry point on any machine: read it, run `make dev`, continue at **Next up**.

## Stage 0 — Walking skeleton

| Task | Status |
|---|---|
| 0.1 Scaffold EMR-only Ozone distro | ✅ Done (2026-08-19) |
| 0.2 `make dev` local dev experience + `docs/dev-setup.md` | ✅ Done (2026-08-19) |
| 0.3 CI (GitHub Actions: build, smoke e2e, publish artifact) | ✅ Done (2026-08-20) — publishes distro zip, not image (ADR-0002) |
| 0.4 Staging VM (in-country, provisioned by playbook) | ⏸ **Deferred to pre-launch** (founder decision 2026-08-21) — build/verify all patient & provider workflows locally + CI first; stand up staging before clinician UAT / pilot prep |
| 0.5 Conventions doc | ✅ Done (2026-08-19) |
| 0.6 Non-code: DPC registration, OpenMRS Talk/Slack intro | ⬜ Founder action |

**Test Gate 0:** passed in local+CI form (one-command boot, clean `destroy → dev` rebuild, empty DB, EMR-only container set, CI green with smoke e2e). Staging-dependent items (gate 1 & 3) move to the pre-launch checklist with task 0.4.

## Stage 1 — Ghana patient registry ← **current stage**

| Task | Status |
|---|---|
| 1.1 Identifier types (Ghana Card, NHIS, folder number + ID-Gen, legacy) | ✅ Done (2026-08-21) — NHIS 8-digit format still provisional pending a physical card |
| 1.2 Person attributes (phones, emergency contact, occupation) | ✅ Done (2026-08-21) — regex enforcement lands with 1.4 form config |
| 1.3 Address hierarchy (16 regions / 261 MMDAs) | ✅ Done (2026-08-21) — validator in CI asserts 16/261 |
| 1.4 Registration form config | ✅ Done (2026-08-21) — Ghana Card/NHIS/legacy pinned, phone regex, estimated age, cascading address |
| 1.5 Duplicate guard + SOP | ✅ Done (2026-08-22) — SOP + uniqueness; no similar-patient feature in reg app 6.1.0, no matching engine per plan |
| 1.6 Roles & users (Front Desk, Clinician, Clinic Admin, Support) | ✅ Done (2026-08-22) — REST-level isolation verified; FHIR privilege gap documented for Stage 3 |
| 1.7 Locations | ✅ Done (2026-08-21) — org → branch → rooms per ADR-0003; demo sites retired |
| 1.8 Seed & performance tool (5,000 patients) | ✅ Done (2026-08-22) — 5,052 seeded @54/s; worst p95 666ms (gate <2s) |
| 1.9 Playwright specs in CI | ✅ Done (2026-08-22) — 6 passing + 1 fixme (FHIR gap); search by all 5 paths |

Pending decisions/inputs:
- Verify NHIS number format on a current NHIA card (needed before locking 1.1 validation)
- Cloud provider for staging VM (deferred with 0.4 to pre-launch)

## Session log

### 2026-08-22 (later) — Tasks 1.8 + 1.9; Test Gate 1 local checks (laptop)
- `tools/seed-patients/seed.py`: 5,052 synthetic Ghanaian patients (names/phones/districts, ~70% Ghana Card, ~60% NHIS) at 54/s; requests folder numbers AND OpenMRS IDs from Idgen (both types are required). Built-in latency report.
- **Test Gate 1 status (local+CI form):** #2 specs green ✓ · #3 worst search p95 = 666ms < 2s ✓ · #4 malformed values rejected ✓ (spec) · #5 FHIR Patient?identifier=<ghana-card> returns correctly-typed identifiers ✓ · #6 Front Desk blocked (REST) ✓ with FHIR gap documented · #1 (non-author UAT on staging) deferred with 0.4 to pre-launch.
- `search.spec.ts`: one patient found via name, folder number, Ghana Card, NHIS, phone through the real UI.

### 2026-08-22 — Tasks 1.5 + 1.6 (laptop)
- 1.5: front-desk search-before-create SOP (`docs/sop/front-desk-registration.md`); registration app 6.1.0 has no similar-patient feature — guard = SOP + identifier uniqueness (no matching engine, per plan).
- 1.6: four Sankofa roles in `configuration/roles/roles.csv`. Stock refapp roles all inherit Privilege Level: High (cosmetic separation!) — Front Desk instead carries a computed 132-privilege list (High minus clinical data minus Manage). Privilege matrix: `docs/security/roles.md`. Dev users via `tools/create-dev-users.sh`; e2e proves frontdesk registers+searches but gets 403 on encounters/obs while clinician gets 200.
- **Security finding:** fhir2 module does not enforce privileges — front desk can read Observations via FHIR R4 (confirmed with seeded vitals). REST (what the UI widgets use for clinical data) is enforced. Documented in roles.md, `test.fixme` marker in suite, hardening decision deferred to Stage 3 (module upgrade / upstream PR / proxy rule).
- Gotchas: role description column max 255 chars; watch persistent shell cwd (a stray `e2e/configuration/` from a mis-cwd'd mkdir).

### 2026-08-21 (later) — Tenancy decision + task 1.7 (laptop)
- ADR-0003: tenancy is organization-scoped — one instance per organization, branches as Locations, patients org-wide; cross-organization sharing deferred to the HIE layer over FHIR (founder direction: cross-org is the eventual goal).
- Locations config: Sankofa Medical Centre → Accra Branch → Registration/Triage/Consultation 1-2 (Login+Queue tags); all 114 demo locations (Site 1-46, Wards, etc.) retired by UUID.
- e2e helpers log in at Registration; smoke spec deduplicated through helpers.ts.
- 1.4 registration form: `sankofa-frontend-config.json` served via SPA_CONFIG_URLS (compose override + pom copy step — parent only copies docker-compose-files.txt/start-ozone.sh); pins Ghana Card/NHIS/legacy identifier fields, NCA phone regex on all three phone fields, estimated DOB on, cascading Region→District address, photo capture off, dead Odoo/SENAITE/Superset nav links removed. Makefile fixed: `make dev` always rebuilds (stale target/ served old config silently).
- 1.3 address hierarchy: Ghana → 16 regions → 261 MMDAs loaded via Address Hierarchy module (GSS-derived dataset, cleaned: Juaboso dupe dropped, Eastern renamed); Town stays free text; `tools/validate-address-hierarchy.py` asserts 16/261 in CI.
- 1.2 person attributes: Telephone Number made searchable; added Alternate phone, Emergency contact name/phone, Occupation. Phone regex (`^0[235]\d{8}$`, NCA-derived) enforced at the form layer in 1.4.

### 2026-08-21 — Identifier research + task 1.1 (laptop)
- `docs/research/patient-identifiers.md`: Ghana ID landscape (verified: Ghana Card mandatory-but-not-exclusive, NHIS↔Ghana Card linkage via *929#, NCA phone format/MNP) + EMR identifier best practice; recommendation table for 1.1.
- Key deviation from master plan: Ghana Card regex widened to `^[A-Z]{3}-\d{9}-\d$` (ICAO nationality prefixes — non-citizens' cards aren't `GHA-`).
- Implemented 1.1 as Initializer config: 4 identifier types + Idgen sequential source (prefix `ACC`, Mod-30 alphabet only — no B I O Q S Z, or generation throws) + autogeneration (auto on, manual off). Generated format is hyphenless: `ACC0000015`.
- Verified end-to-end: registration UI auto-generates valid folder numbers (new `registration.spec.ts`); malformed Ghana Card/NHIS rejected via REST; folder number enforced as required.
- Gotcha documented: `make build` deletes `target/` under running containers' bind mounts → restart openmrs+frontend after building.

### 2026-08-20 — CI (laptop)
- Remote established at `github.com/Obed-Ababio/sankofa-ehr` (old repo history replaced; pre-Ozone iteration preserved as local tag `archive/pre-ozone-remote` on the laptop).
- `e2e/` Playwright project: smoke spec logs in as admin, handles the location picker, asserts the home dashboard. Runs locally (`cd e2e && npx playwright test`) and in CI.
- `.github/workflows/ci.yml`: hygiene job (Zone.Identifier check, gitleaks full-history secret scan) + build job (build distro → boot stack → wait for health → smoke e2e → assert EMR-only services → upload distro zip from main).
- ADR-0002: deployable artifact is the distro zip, not a container image.

### 2026-08-19 — Stage 0 kickoff (laptop)
- Repo initialized; monorepo layout per plan §1; Ozone distro scaffolded from `com.ozonehis:maven-archetype` (parent `1.0.0-alpha.10`, O3 `3.0.0-beta.18`).
- EMR-only profile via `distro/scripts/docker-compose-files.txt` (common + openmrs only); demo data off; ADR-0001 records it all.
- `configuration/` is a symlink into `distro/configs/openmrs/initializer_config` — always clone this repo on a Unix filesystem (macOS or *inside* WSL2, never a Windows drive path).
- Verified: `make dev` boots healthy stack at http://localhost (admin/Admin123); FHIR patient count 0; full `make destroy && make dev` rebuild works.
- Machine-local setup (repeat per machine, see [dev-setup.md](dev-setup.md)): Docker runtime, Maven, Ozone profile in `~/.m2/settings.xml`.
