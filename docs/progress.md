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
| 1.2 Person attributes (phones, emergency contact, occupation) | ⬜ |
| 1.3 Address hierarchy (16 regions / 261 MMDAs) | ⬜ |
| 1.4 Registration form config | ⬜ |
| 1.5 Duplicate guard + SOP | ⬜ |
| 1.6 Roles & users (Front Desk, Clinician, Clinic Admin, Support) | ⬜ |
| 1.7 Locations | ⬜ |
| 1.8 Seed & performance tool (5,000 patients) | ⬜ |
| 1.9 Playwright specs in CI | ⬜ |

Pending decisions/inputs:
- Verify NHIS number format on a current NHIA card (needed before locking 1.1 validation)
- Cloud provider for staging VM (deferred with 0.4 to pre-launch)

## Session log

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
