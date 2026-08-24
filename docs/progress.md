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

## Stage 1 — Ghana patient registry — ✅ CLOSED in local+CI form (2026-08-22, `v0.1`)

Gate checklist: [gates/gate-1-patient-registry.md](gates/gate-1-patient-registry.md). Staging-dependent items (gate #1 UAT) deferred to pre-launch. **Next stage: Stage 2 — OPD clinical workflow** (first task 2.1: OCL/CIEL concept dictionary + Ghana OPD diagnosis value set — needs clinician sign-off).

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

## Stage 2 — OPD clinical workflow (in progress)

| Task | Status |
|---|---|
| 2.1 Concept dictionary + Ghana OPD diagnosis value set | 🔶 Draft ready (2026-08-22) — 155-entry CIEL-resolved draft awaiting **clinician sign-off** (`docs/clinical/ghana-opd-value-set.md`); load blocked on sign-off + founder's OCL account |
| 2.2 OPD visit type | ✅ Done (2026-08-22) — OPD Visit is the only active visit type (others retired via Initializer); start/stop proven on the chart by e2e |
| 2.3 Vitals & biometrics | ✅ Done (2026-08-22) — standard O3 vitals app; ranges verified, temperature thresholds added (36–37.4 normal, 35/40 critical — **clinician to confirm** with value set); BMI auto-computes |
| 2.4 Consultation form | ⬜ |
| 2.5 Medications (NHIS list) | ✅ Done (2026-08-24) — 424 formulations from the NHIS Medicines List 2025 (levels ≤C), CIEL-mapped, demo drugs retired; order basket verified by e2e; 16 unmapped items flagged for clinician (`docs/clinical/ghana-formulary.md`) |
| 2.6 Prescription printing | ⬜ |
| 2.7 Service queues | ✅ Done (2026-08-23) — Triage + Consultation queues via Initializer; check-in queues from the start-visit form; board + transfer proven by e2e (real-time two-browser check stays a Gate 2 manual item) |
| 2.8 Chart review | ⬜ |
| 2.9 FHIR contract test in CI | ⬜ |

Pending decisions/inputs for 2.1:
- **Founder: line up the Ghanaian clinician** to review `docs/clinical/ghana-opd-value-set.md` (7 open questions are listed in the doc)
- **Founder: create an OCL account** (openconceptlab.org — anonymous API access was disabled, so both the collection and any API verification need an account) and an org for Sankofa
- Upstream: CIEL 122604 (cholera) lacks an ICD-10 map; CIEL 86 (motor vehicle accident) carries a wrong map (N25.8) — report both to CIEL once we have the OCL/Talk account

## Session log

### 2026-08-24 — Task 2.5 NHIS medications (laptop)
- Parsed the **NHIS Medicines List March 2025** (NHIA PDF, 551 formulations, prices + prescribing levels) into `tools/drugs/nhis-ml-2025.csv`. Scope = levels A/M/B1/B2/C (441); D/SM excluded. `tools/drugs/build-formulary.py` resolves each generic + dosage form to CIEL (pins.csv holds 79 curated overrides: UK→US spellings, NHIA typos like "Ceftriazone"/"Ephedrine HCI", combos, insulin naming) and emits `configuration/drugs/drugs-sankofa.csv` (424; one NHIS pack-size duplicate deduped) plus `configuration/concepts/concepts-sankofa_2_drug_concepts.csv` (227 referenced CIEL concepts created under canonical CIEL uuids — full CIEL via OCL later updates them in place). 16 formulations have no CIEL concept (BP/BPC galenicals like Aqueous Cream, Simple Linctus) — flagged for clinician in `docs/clinical/ghana-formulary.md` (review doc; NHIA already curated the list, so it ships to dev and gets reviewed rather than blocking).
- Demo cleanout: 320 demo drugs + 469 demo drug concepts retired via overlays (demo concepts use random uuids and their names collide with CIEL FSNs — `DuplicateConceptNameException` until retired first).
- **Provider gap found:** EMR-only profile ships no provider record for admin — the drug-order workspace crashes on `currentProvider.uuid` ("An error has occurred"). e2e now self-provisions an idempotent provider ("superuser"); real provider management lands with clinician users.
- Initializer gotchas that cost hours: within a domain, files are processed in a fixed order and a file's checksum is recorded even when lines FAIL — a failed file won't retry until its content changes (bump a header, e.g. `_order`); startup copies `distribution/openmrs_config` → `data/configuration` but never DELETES renamed/removed files (stale CSVs keep loading — `docker exec … rm` them or destroy volumes); name our retire-then-create pairs so lexical order matches intent (`…_1_retire…`, `…_2_…`).
- e2e: `formulary.spec.ts` (REST: exactly 424 active drugs, NHIS staples present, demo drugs gone; UI: order basket search finds Artemether+Lumefantrine during an OPD visit). Suite hardened: queue entries outlive visits → `endAllActiveQueueEntries` helper + `workers: 1` (parallel specs raced shared queue state); REST asserts after UI saves poll (toasts fire before data is queryable); specs that start visits must fill the queue fields or a broken `queue-entry-number` call blocks the workspace.

### 2026-08-23 — Task 2.7 service queues (laptop)
- Queue metadata (statuses/priorities/services concept sets, queue-tagged room locations) already shipped with the O3 base config — what was missing was the queues themselves. **Initializer upgraded 2.6.0 → 2.12.0** (the `queues` domain only exists from 2.7.0; distro pom now swaps the refapp-bundled omod). `configuration/queues/queues-sankofa.csv`: Triage @ Triage, Consultation @ Consultation Room 1 (both consultation rooms share one queue).
- Check-in flow: `showServiceQueueFields: true` in the Sankofa frontend config puts Queue Location/Service/Priority on the start-visit form; `Queue number` visit attribute type created with the O3 default uuid (`c61ce16f…`) so queue numbers save.
- **MariaDB fix:** the queue module's ~25-table join drove MariaDB's exhaustive join-plan search (optimizer_search_depth=62) into minutes-long "Statistics" hangs — every queue-entry call 504'd and stuck queries piled up. `--optimizer-search-depth=0` added to the mysql service (compose override replaces the whole `command` list, so it repeats the base flags). Queue-entry now ~150 ms.
- `make dev` now also self-heals the proxy: when compose *recreates* containers (config change), nginx holds stale upstream IPs and everything 502s — dev restarts the proxy if the config assert fails.
- e2e `queues.spec.ts`: register → check in (queue fields on start-visit form) → REST-assert Waiting entry in Triage queue → board shows the row under View→Triage → Transfer modal → patient under Consultation Room 1 → REST-assert the single active entry moved. Board quirk: it scopes to the login location by default; the old view keeps the stale row until the next poll (not asserted).

### 2026-08-22 (Stage 2, later) — Tasks 2.2 + 2.3 (laptop)
- 2.2: `configuration/visittypes/visittypes-sankofa.csv` keeps OPD Visit (base O3 uuid `287463d3…`) and retires Facility/Home/Offline/Group Session, so the start-visit form offers exactly one type. Base visit types come from Ozone's `visittypes-core_data.csv`; our overlay file sorts after it, so Initializer applies the retirements last.
- 2.3: vitals concepts (CIEL 5085-5092 uuids) already carry sensible ranges from the O3 starter set **except temperature — no normal/critical thresholds, so fever never flagged**. Added `configuration/concepts/concepts-sankofa_vitals_ranges.csv` (full concept row for CIEL 5088; partial rows would blank other fields): 36–37.4 normal, 35/40 critical, 25/43 absolute. Standard adult defaults — clinician to confirm alongside the value set.
- e2e `opd-visit.spec.ts`: register → start OPD visit (asserts it's the only type) → record 8 vitals incl. temp 38.5 → REST-assert obs + ranges → end visit → REST-assert single closed OPD visit. Plus a REST spec pinning the active visit-type list.
- O3 UI gotchas hit: the patient-banner mega-button's accessible name contains "Actions" (use `exact: true`); the siderail trigger and form submit share the name "Start a visit"; the visit header has its own "End visit" button and the confirm modal's button has the same name (scope to the dialog); "Record vitals"/"Record vital signs" both exist on an empty chart.

### 2026-08-22 (Stage 2 kickoff) — Task 2.1 draft value set (laptop)
- Ghana OPD diagnosis value set drafted: 155 diagnoses across 20 categories (GHS top OPD causes + plan §4 list), every entry resolved to a live CIEL concept with its ICD-10-WHO mapping captured. Deliverables: `docs/clinical/ghana-opd-value-set.md` (clinician review doc with sign-off block + 7 open questions) and `ghana-opd-diagnoses-draft.csv` (machine-readable, source of truth).
- Tooling: `tools/concepts/resolve-value-set.py` (resolve terms→CIEL / verify a value-set CSV; retries; `CIEL_BASE_URL` overridable) + `render-value-set-table.py` (regenerates the doc table from the CSV). Terms curated in `tools/concepts/ghana-opd-terms.csv`; 36 entries pin explicit CIEL ids where name search picks a sibling concept.
- **OCL's API dropped anonymous access** (2024/25 pricing change) — resolution runs against a CIEL-loaded OpenMRS instead (Bahmni standard demo has full CIEL; OpenMRS dev3 is only a subset; demo/o3/qa-refapp servers are behind Cloudflare). Founder needs an OCL account before the collection can be created.
- CIEL upstream issues found: 122604 cholera has no ICD-10 map; 86 "Accident, motor vehicle" maps to N25.8 (renal!) — flagged in the review doc, to be reported.
- Load path confirmed present in the distro: OCL module 2.2.0 + Initializer 2.6.0 (`ocl` domain); current dictionary is the 1,197-concept O3 starter set with CIEL source registered.

### 2026-08-22 (verification) — Milestone check before Stage 2 (laptop)
- Independent re-verification of gates 0+1: CI green on the gate-closing commit, all claimed artifacts present, FHIR typed-identifier query ✓, 5,056 patients in DB, name search ~130 ms, fhir2 privilege gap re-confirmed live (frontdesk 200 on FHIR obs, blocked on REST).
- Gap fixed: `search.spec.ts` was only green in CI because CI's DB is empty — on a seeded DB, `getByRole('searchbox').first()` matched the home page's disabled "Filter table" input and failed deterministically. Locator scoped to the patient-search placeholder; suite green against the 5k-patient DB.
- Gap fixed: `make dev` on an already-running stack rebuilt target/ under the containers' bind mounts without restarting them — frontend silently served 404 for `sankofa-frontend-config.json` (entire Ghana form config gone). `dev` target now restarts openmrs+frontend when the stack was up, waits for health, and asserts the config is served.

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
