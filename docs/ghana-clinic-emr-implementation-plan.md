# Ghana Clinic EMR — Staged Implementation Plan (v1.0)

**Product:** Multi-clinic EMR for Ghanaian private clinics, built on OpenMRS 3 via Ozone distro tooling. Clinics first, hospital tier later.
**Team:** 2–5 engineers. **Method:** Each stage ends in a working, tested system. A stage is closed only when its Test Gate passes; then we tag a release and move on.

---

## 0. Engineering principles (from AGENTS.md) → project practices

| Guideline | What it means in this project |
|---|---|
| No backward compatibility | Pre-pilot: config/schema changes are destructive — reset staging DB, never write compat shims. Post-pilot: one-way Liquibase changesets inside our own modules; migrate once, delete the old path in the same release. No dual-writes, no legacy endpoints, no long-lived feature flags. |
| Simplest implementation that fully meets current requirements | EMR-only Ozone profile (Odoo/SENAITE/Superset/Keycloak OFF). OpenMRS built-in auth until a second app exists. No custom control-plane app; Ansible + inventory is the fleet manager. No patient-matching engine; search-before-create SOP. |
| Grow in layers; never trade a working product for unfinished complexity | The stage structure below. Nothing merges to `main` that breaks the end-to-end demo. Timeboxed spikes end in a decision (ADR), not half-built code. |
| Modular, separated concerns | Zero forks of upstream. All our work lives in: `configuration/` (metadata-as-code), `esm-gh-*` (frontend microfrontends), `omod-gh-*` (backend modules), `tools/`, `infra/`. Upstream fixes are PR'd upstream. |
| Prefer established libraries | Use existing OpenMRS modules first: Initializer, ID-Gen, Address Hierarchy, FHIR2, REST, Reporting, Service Queues, Form Builder/Form Engine. A custom module requires a written reason (ADR). |
| Lean on existing dependencies; check docs before rebuilding | Every "OpenMRS can't do X" claim must cite the doc/Talk thread checked. 2-day max evaluation spikes before any build decision. |
| Long-term architectural decisions, no stopgaps | Coded data from day one (ICD-10 diagnoses, NHIS drug list, Ghana Card/NHIS identifiers) because claims depend on it. FHIR R4 is the only external integration boundary. Config-as-code only — no hand-edits in admin UI on staging/prod. Decisions recorded as ADRs in `docs/adr/`. |

---

## 1. Locked architecture

- **Distro:** Ozone FOSS scaffold, apps limited to OpenMRS 3 (platform 2.6.x+, O3 frontend). Other Ozone apps are switched on later, not re-integrated later — that is the upgrade path to hospital tier.
- **Tenancy:** one containerized instance per clinic (hard data isolation → Data Protection Act story), managed as a fleet from one Ansible inventory. Same image everywhere; only config + secrets differ.
- **Data model:** OpenMRS MariaDB. Identifiers: Ghana Card PIN, NHIS number, auto-generated clinic folder number. Diagnoses coded (CIEL→ICD-10). Drugs from NHIS Medicines List.
- **Integration boundary:** OpenMRS FHIR2 (R4) + REST. All future connections (billing, claims, DHIMS2, SMS, GHIMS HIE) consume these APIs.
- **Deployment profiles:** (A) cloud VM per clinic in-country + clinic 4G failover router; (B) "clinic box" (mini-PC + UPS, local compose, encrypted nightly sync to cloud) for poor-connectivity sites. Identical release artifact.
- **Licensing/IP:** monorepo is private. Changes to upstream MPL-2.0 files are minimized and PR'd upstream (keeps obligations at zero and reduces maintenance). Proprietary value (SMS, DHIMS2 export, claims engine later) lives in our own separately-licensed modules.

**Monorepo layout (`gh-emr/`):**

```
distro/            # Ozone scaffold: docker compose, app versions, proxy config
configuration/     # OpenMRS Initializer domains (all metadata as CSV/JSON)
frontend/esm-gh-*/ # custom O3 microfrontends (only when an ADR justifies one)
backend/omod-gh-*/ # custom OpenMRS modules (same rule)
tools/             # import CLI, seed-data generator, report tally scripts
infra/             # Ansible playbooks, inventory, SOPS-encrypted secrets
docs/              # runbooks, ADRs, training scripts, gate checklists
.github/workflows/ # CI
```

**Team split (adjust to actual headcount):**
- **Eng 1 — Tech lead (Java/infra):** distro, backend modules, CI/CD, backups, security.
- **Eng 2 — Frontend (React/TS):** O3 config, forms, custom ESMs, printing.
- **Eng 3 — Full-stack/QA/implementation:** Initializer config, Playwright e2e, UAT scripts, clinic training, data imports.
- **Eng 4–5 (if present):** split frontend/backend; one owns reporting + integrations.

---

## 2. Stage 0 — Walking skeleton (Weeks 0–2)

**Goal:** a versioned, reproducible, deployable empty EMR with CI and one automated end-to-end test.

**Tasks**
- **0.1** Scaffold distro from Ozone FOSS tooling; disable Odoo, SENAITE, Superset, Keycloak. Pin every component version in `distro/`. Commit an ADR: "EMR-only profile; built-in OpenMRS auth until a second app ships."
- **0.2** Local dev experience: `make dev` → compose stack up with demo data OFF; documented in `docs/dev-setup.md`. Target: new laptop → running system < 30 min.
- **0.3** CI (GitHub Actions): on PR — build distro image, run backend unit tests, boot compose, run Playwright smoke (login → dashboard). On `main` merge — push image to GHCR tagged with semver + git SHA, auto-deploy staging.
- **0.4** Staging: 1 VM in-country (min 4 vCPU / 8 GB / 100 GB NVMe, Ubuntu 24.04, Docker) behind Caddy or Traefik with automatic TLS. Provisioned by `infra/` playbook, not by hand.
- **0.5** Conventions doc: trunk-based development, short-lived branches, conventional commits, ADR template, rule that staging/prod metadata comes only from `configuration/` (Initializer) — the DB is disposable until pilot.
- **0.6** Non-code: submit Data Protection Commission registration; create OpenMRS Talk + Slack accounts; post an intro (community support is a dependency we lean on).

**Test Gate 0 — all must pass**
1. Fresh clean VM → one documented command → login page reachable over HTTPS in < 15 min.
2. CI green: image builds reproducibly from a tag; smoke e2e passes in pipeline.
3. Staging can be destroyed and rebuilt from scratch by the playbook alone.
4. Any team member can run `make dev` successfully (verified by the newest member).

---

## 3. Stage 1 — Ghana patient registry (Weeks 2–5)

**Goal:** register, store, and find Ghanaian patients correctly. This is the "stores patient data" MVP core.

**Tasks**
- **1.1 Identifier types** (`configuration/patientidentifiertypes/`):
  | Identifier | Format/validation | Required | Unique | Generation |
  |---|---|---|---|---|
  | Ghana Card PIN | regex `^GHA-\d{9}-\d$` | No (not all patients carry it) | Yes | Manual entry |
  | NHIS membership no. | 8 digits `^\d{8}$` — **confirm against a current NHIA card in week 2** | No | Yes | Manual entry |
  | Clinic folder number | prefix per clinic (e.g. `ACC-`) + zero-padded sequence + Luhn mod-30 check digit | Yes (primary) | Yes | ID-Gen module, `SequentialIdentifierGenerator` |
  | Legacy folder no. | free text | No | No | Manual (used by the Stage 4 importer) |
- **1.2 Person attributes** (`personattributetypes/`): phone (`^0\d{9}$`), alternate phone, emergency contact name + phone, occupation. Nothing else — attributes are added when a form needs them, not speculatively.
- **1.3 Address hierarchy:** load Region (16) → District (261 MMDAs) → Town (free text) via the Address Hierarchy module. Deliverable: `configuration/addresshierarchy/gh-regions-districts.csv` generated from the Ghana Statistical Service MMDA list; a validation script asserts the counts (16 / 261).
- **1.4 Registration form:** configure `@openmrs/esm-patient-registration-app` (config-schema, no code): sections Demographics / Identifiers / Contact / Address; required: given name, family name, sex, DOB **with estimated-age support ON** (many patients know age, not birth date); folder number auto-filled.
- **1.5 Duplicate guard:** enable the registration app's similar-patient check; SOP for front desk: search by Ghana Card or phone before creating. No matching engine (ADR: revisit only if pilot data shows a real duplicate rate).
- **1.6 Roles & users** (`roles/`, `users/`): Front Desk (register/search only — must NOT hold encounter-view privileges), Clinician, Clinic Admin, Support (read-only). Privilege matrix documented in `docs/security/roles.md`.
- **1.7 Locations** (`locations/`): clinic root, Registration, Triage, Consultation Room 1..n; login locations configured.
- **1.8 Seed & performance tool** (`tools/seed-patients/`): generate 5,000 synthetic patients with realistic Ghanaian names/phones via FHIR API; script prints search p95 latency.
- **1.9 Playwright specs added to CI:** register with/without Ghana Card; each validation failure shows a clear message; search by name, folder no., Ghana Card, NHIS no., phone; Front Desk cannot open a clinical chart.

**Test Gate 1**
1. QA/implementation engineer (not the author) executes the 10-step registration UAT script on staging without developer help.
2. All Stage-1 Playwright specs green in CI.
3. With 5,000 seeded patients: search p95 < 2 s.
4. Malformed Ghana Card / NHIS / phone values are rejected with human-readable errors.
5. `GET /ws/fhir2/R4/Patient?identifier=<ghana-card>` returns the patient with all identifiers correctly typed.
6. Front Desk role verifiably blocked from clinical data (manual + e2e check).

---

## 4. Stage 2 — OPD clinical workflow (Weeks 5–8)

**Goal:** a complete outpatient visit: queue → vitals → coded consultation → prescription → closed visit, producing clean FHIR resources.

**Tasks**
- **2.1 Concept dictionary:** create an OCL organisation + collection sourced from CIEL; load via Initializer. Deliverable: **Ghana OPD diagnosis value set (~150 CIEL concepts, all ICD-10-mapped)** — malaria, URTI, hypertension, T2DM, typhoid, UTI, gastroenteritis, anaemia, asthma, skin/soft-tissue infections, RTA injuries, ANC-related encounters, etc. Reviewed and signed off by a Ghanaian clinician before load. Owner: Eng 3 + clinical advisor.
- **2.2 Visit type:** `OPD Visit`; visit start/stop wired into the O3 patient chart.
- **2.3 Vitals & biometrics:** standard O3 vitals app; concepts: weight, height (auto-BMI), temperature, pulse, respiratory rate, BP systolic/diastolic, SpO2; abnormal-range flags configured.
- **2.4 Consultation form** (O3 Form Builder → JSON in `configuration/ampathforms/`): Presenting complaint (text) · History (text) · Examination (text) · **Diagnoses via the O3 diagnosis widget** (coded, multiple, primary/secondary rank — becomes FHIR `Condition`; never plain-text dx) · Investigations advised (text for now) · Treatment plan (text) · Review date (date). Forms are versioned files; changing a form = new file version, old one deleted (no compat layers).
- **2.5 Medications:** `configuration/drugs/` seeded with ~250 common formulations from the **NHIS Medicines List** (name, strength, form). O3 order basket enabled for outpatient prescriptions.
- **2.6 Prescription printing:** 1-day spike on O3's built-in print. If insufficient → first custom ESM `esm-gh-prescription-print` (react-to-print, A5 + 80 mm layouts). ADR records the outcome either way.
- **2.7 Service queues:** enable the O3 Service Queues app: Registration → Triage → Consultation; front desk adds to queue at check-in; statuses visible in real time. No waiting-room TV screen in MVP.
- **2.8 Chart review:** patient summary widgets configured — vitals trend, active visit, past visits, past diagnoses.
- **2.9 FHIR contract test (CI):** after the scripted e2e journey, assert existence and correct coding of `Encounter`, `Observation` (vitals), `Condition` (dx with ICD-10 code + rank), `MedicationRequest`. This test is the permanent guarantee that future integrations (claims, DHIMS2, GHIMS) have the data they need.

**Test Gate 2**
1. A friendly Ghanaian clinician, after a 30-minute orientation, completes 5 scripted patient scenarios unaided.
2. Full-journey Playwright spec + FHIR contract tests green in CI.
3. Timed run: registration → printed prescription ≤ 6 min for a standard case.
4. Queue reflects moves in real time across two browsers.
5. Every diagnosis saved in the demo dataset carries an ICD-10 mapping (SQL/FHIR audit script returns zero unmapped).

---

## 5. Stage 3 — Reports, backup/restore, hardening (Weeks 8–10)

**Goal:** the system is safe to hold real patient data and useful to a clinic owner on day one.

**Tasks**
- **3.1 Reports** (OpenMRS Reporting module, SQL dataset definitions — no custom reporting service):
  - *Daily OPD Register*: one row per encounter — folder no., name, sex, age, NHIS status, new/repeat attendance, diagnoses.
  - *Monthly Morbidity Summary*: counts by diagnosis category × age band × sex, **laid out to mirror the GHS OPD morbidity return** (pre-work for Stage 5's DHIMS2 export).
  - *Patient master list* export.
  - Delivery: CSV/XLS download + a cron container that renders the daily register and emails it to the clinic admin via SMTP relay.
- **3.2 Backups:** nightly cron container: `mariadb-dump --single-transaction` + `configuration/` + document uploads volume → tar → `age`-encrypt → `rclone` to in-country object storage; weekly copy to a second provider. Retention 30 daily / 12 monthly. Every run pings a heartbeat monitor; a missed ping alerts the team.
- **3.3 Restore:** `infra/restore.sh` rebuilds a working instance from any backup on a clean VM. Targets: **RPO ≤ 24 h, RTO ≤ 4 h.** (Binlog shipping for RPO < 1 h is deliberately deferred — ADR — until clinics are paying.)
- **3.4 Hardening checklist (~20 items, versioned in `docs/security/`):** HTTPS-only with auto-renewing certs; admin/DB/SSH reachable only over WireGuard/Tailscale; SSH keys-only + fail2ban; demo users removed; password policy + 15-min session timeout; login rate-limit at proxy; MariaDB not exposed; unattended OS security updates; Trivy image scan in CI.
- **3.5 Audit trail:** rely on OpenMRS native row metadata (creator, date_created, changed_by) + authentication/access logs shipped off-box (rsyslog → object storage or Loki). Timeboxed 2-day spike on the community audit-log module: adopt only if O3-compatible and maintained; otherwise ADR and move on.
- **3.6 Monitoring:** Uptime Kuma (or equivalent): HTTP health, disk %, cert expiry, backup heartbeat; alerts to the team channel via webhook.
- **3.7 Compliance pack:** DPC registration submitted (from Stage 0) and tracked; clinic-facing privacy notice poster; data-processing agreement template for clinic contracts; patient data-access-request SOP.

**Test Gate 3**
1. **Restore drill:** yesterday's backup → clean VM → working system with data intact, within 4 h, executed from the runbook by an engineer who didn't write it.
2. Simulated failures on staging: disk-full and hard power-cut both recover cleanly (MariaDB integrity check passes).
3. All ~20 hardening checklist items pass; TLS config scores A on SSL Labs (or equivalent).
4. Reports match a hand-tallied seeded dataset exactly (tally script in `tools/`).
5. 7-day staging soak with nightly synthetic load: zero unhandled 5xx; backups 7/7 green.

---

## 6. Stage 4 — Pilot go-live at design-partner clinics (Weeks 10–14)

**Goal:** 1–2 real clinics running daily operations on the system for 4 weeks.

**Tasks**
- **4.1 Deployment profiles finalized:**
  - *Profile A (cloud):* per-clinic VM (4 vCPU / 8 GB / 100 GB) in-country; clinic side gets a 4G failover router (e.g., Teltonika-class).
  - *Profile B (clinic box):* mini-PC (≥16 GB RAM / 512 GB NVMe) + ≥1 kVA line-interactive UPS; local compose; nightly encrypted DB push to cloud when online; remote support via Tailscale.
  - Same versioned image for both. Choice per clinic is a one-line inventory variable.
- **4.2 Provisioning:** `ansible-playbook site.yml -l <clinic>` — from inventory entry to running, TLS'd, monitored, backed-up instance in one idempotent command. Secrets via SOPS/age.
- **4.3 Legacy import CLI** (`tools/import-patients/`): clinic fills a provided XLSX/CSV template (name, sex, DOB or age, phone, NHIS no., Ghana Card, old folder no.); tool runs `--dry-run` producing a validation report, then imports idempotently via FHIR (skips existing by identifier; old folder number stored as *Legacy folder no.* so paper folders stay findable — critical adoption detail).
- **4.4 Training package:** 2-hour front-desk script, 2-hour clinician script, laminated quick-reference cards, a sandbox instance that resets nightly.
- **4.5 Go-live checklist per clinic** (`docs/golive-checklist.md`): connectivity + failover tested, printer test page, users created with correct roles, legacy import signed off by the clinic, privacy poster displayed, hypercare plan (engineer on-site days 1–2, daily check-in week 1). Set expectation explicitly: **cash book stays manual until Stage 5** — billing is out of scope.
- **4.6 Support ops:** WhatsApp Business line + a single ticket board; SLA: first response < 2 h during clinic hours; weekly triage converts feedback into backlog items. **Change-control rule:** no mid-pilot scope additions unless data-loss or a hard blocker (guideline: never trade a working product for unfinished complexity).

**Test Gate 4 — measured per clinic over 4 weeks**
1. ≥ 95% of OPD encounters captured in the system by week 4 (verified against the clinic's paper tally).
2. Median new-patient registration ≤ 3 min.
3. Uptime ≥ 99% of clinic hours; zero data-loss incidents.
4. One restore drill executed from a *real* pilot backup into staging.
5. Clinic owner signs the pilot-completion review.
**Program gate to Stage 5:** ≥ 2 clinics pass + written willingness to pay.

---

## 7. Stage 5 — First sellable release: billing, DHIMS2 return, SMS (Months 4–6)

**Goal:** the product a clinic pays for.

**Tasks**
- **5.1 Billing decision spike (timeboxed 1 week):** evaluate the community O3 billing module (KenyaEMR lineage) against: service catalogue with prices; bill auto-composed from the visit's orders + manual line items; cash & mobile-money payment recording; receipt print; day-end cashier report; void-with-reason + audit. Score ≥ 70% fit → adopt and extend; below → enable Ozone's Odoo profile for billing instead. **Hand-rolling billing from scratch is not an option** (established-libraries guideline). Output: ADR + chosen path.
- **5.2 Implement chosen billing path.** Receipt on 80 mm thermal via print CSS; price list lives in `configuration/`; MoMo handled as recorded reference number (direct MTN MoMo API integration deferred — ADR).
- **5.3 Day-end reconciliation report** by cashier and payment mode; must reconcile to the pesewa.
- **5.4 DHIMS2 monthly OPD return:** deliverable #1 is a **mapping table (ICD-10 → GHS morbidity categories)** validated against the official OPD morbidity form; generator then produces the monthly summary in that exact layout (XLSX/CSV) for entry into DHIMS2. Direct DHIS2 API push is a later enhancement requiring GHS engagement (ADR).
- **5.5 SMS module** (`omod-gh-sms`, proprietary): provider spike Hubtel vs mNotify (pricing, deliverability across MTN/Telecel/AT); templates (welcome, review-date reminder T-1); **opt-in consent flag captured at registration** (Act 843 compliance); delivery log, retry, global kill-switch.
- **5.6 Fleet ops maturity:** instance inventory, versions, and backup status auto-generated from Ansible inventory + heartbeats into one static status page. Still no custom control-plane app (ADR).
- **5.7 Commercial packaging:** proprietary modules in private repos with a per-instance license flag in config; upstream fixes continue to be PR'd upstream; convert pilot clinics to paid plans (setup fee + cedi-denominated monthly tier).

**Test Gate 5**
1. 20 scripted billing scenarios in UAT reconcile to the pesewa; receipts legible on the actual thermal printer model shipped to clinics.
2. Morbidity return for a seeded month matches a hand tally 100%.
3. SMS delivery ≥ 95% to a test panel across MTN, Telecel, and AT; opt-out honored.
4. Upgrade test: a pilot clinic instance upgrades to the release with zero data loss and < 15 min downtime.
5. **Commercial gate: ≥ 2 clinics on paid plans.**

---

## 8. Stage 6 — Next horizons (Months 6–12, planned not specified)

Each begins with a timeboxed spike + ADR, in this order:
1. **NHIA e-claims module (the commercial wedge):** obtain the current NHIA e-claims spec via a client clinic's provider credentials; map encounter → claim (ICD-10 diagnoses, G-DRG, NHIS Medicines List); batch review UI; submission + rejection tracking/analytics. This is why coded data was mandatory from Stage 1–2.
2. **Dispensing & pharmacy stock:** evaluate the O3 dispensing app for clinic tier; full stock control arrives with Odoo at hospital tier.
3. **Appointments** app enablement + SMS reminders tie-in.
4. **Hospital tier:** switch on the rest of Ozone (Odoo, SENAITE, Superset, Keycloak SSO — one-time scripted user migration), evaluate inpatient/ADT apps. This is the moment the Stage-0 choice of Ozone tooling pays off: components are enabled, not re-integrated.

---

## 9. Testing strategy & Definition of Done (applies to every stage)

- **Pyramid:** JUnit for `omod-gh-*` → REST/FHIR contract tests against the compose stack in CI → Playwright e2e happy paths + role checks → manual UAT scripts executed by a non-author → perf smoke (k6, 25 concurrent users — several times a single clinic's realistic peak).
- **Definition of Done for any task:** merged + CI green (including the *cumulative* e2e suite — earlier stages' tests never get deleted, only updated when behavior intentionally changes) + all metadata expressed in `configuration/` + runbook/ADR updated + demoed on staging.
- **Gate ritual:** end-of-stage demo to the whole team + a clinic advisor; checklist signed and archived in `docs/gates/`; release tagged `v0.<stage>`; only then does the next stage start. A failed gate stops forward work until fixed.

## 10. Top risks → mitigations

| Risk | Mitigation |
|---|---|
| O3 module maturity gaps (billing, printing, audit) | Timeboxed spikes with named fallbacks before any build; active OpenMRS Talk/Slack engagement from week 0. |
| Power cuts corrupting the DB on clinic boxes | UPS mandatory in Profile B; `--single-transaction` dumps; nightly `mariadb-check`; restore drills are gate items, not aspirations. |
| Clinic connectivity | Profile B exists for exactly this; failover router in Profile A. |
| Pilot scope creep | Change-control rule in 4.6; feedback → backlog, not mid-pilot builds. |
| Key-person risk in a 2–5 team | ADRs, runbooks, and the rule that gates are executed by non-authors. |
| NHIS number / NHIA spec assumptions wrong | Verify NHIS format week 2 (task 1.1); begin NHIA engagement during Stage 5, not when building claims. |
| Regulatory (Act 843) | DPC registration starts Stage 0; consent flags, privacy poster, DPA template shipped with pilot. |

## Appendix A — Initializer domains used
`patientidentifiertypes`, `personattributetypes`, `addresshierarchy`, `locations`, `visittypes`, `encountertypes`, `roles`, `privileges`, `users`, `concepts`/`ocl`, `drugs`, `ampathforms`, `globalproperties`, `idgen` (autogeneration options), `queues`.

## Appendix B — Reference formats to verify in-country (week 2)
Ghana Card PIN `GHA-#########-#`; NHIS membership number (8 digits — confirm on a current card); phone `0#########`; 16 regions / 261 MMDAs (re-check count at load time against the current GSS list).

## Appendix C — Sizing quick reference
Per-clinic instance: 4 vCPU / 8 GB RAM / 100 GB NVMe (cloud) or 16 GB mini-PC (clinic box). Staging mirrors production sizing. Object storage: ~1 GB/clinic/year at MVP scope — budget 10× headroom.
