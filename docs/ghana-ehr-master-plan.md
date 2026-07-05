# SANKOFA EHR — Master Plan v1.0

**A cloud-hosted, OpenMRS 3–based electronic health record network for Ghanaian private primary-care clinics.**

| | |
|---|---|
| Document status | Baseline plan — feed to coding agent in stages per Appendix A |
| Date | 2026-07-03 |
| Owner | Solo founder + LLM coding agents |
| Working codename | "Sankofa EHR" (Adinkra: *go back and get it* — apt for retrieving patient records). Trademark/name check is WP-006; treat as placeholder. |
| Target | Live pilot in 2–3 private clinics in Ghana ≈ month 4–6; architecture ready to scale to 50+ clinics and, eventually, national interoperability via Ghana's GHIMS exchange |

**How to use this document:** Sections 1–12 are context and locked decisions. Section 13 is the work-package (WP) catalog — the implementation backlog. Each WP is designed to be handed to a coding agent (e.g., Claude Code) as a standalone task using the prompt template in Appendix A. Do not let an agent contradict Section 3 (Locked Decisions) without recording a new ADR.

---

## 1. Executive summary

Ghana's national EHR program collapsed into a vendor-lock-in crisis in 2024–2025 (LHIMS/Lightwave), and the government's replacement (GHIMS, launched Oct 2025) introduces a state-run Health Information Exchange that third-party systems will eventually plug into. Meanwhile, thousands of private and lower-tier facilities remain paper-based. This project builds an **open-source, cloud-hosted EHR distribution on OpenMRS 3**, sold/offered to **private primary-care clinics as a network**: one logical system where a patient registered at any member clinic can, with consent, be seen with full history at any other member clinic.

Strategy: stay close to upstream OpenMRS (configuration + thin custom modules, not a fork), run **one multi-clinic cloud instance** with row-level data isolation for the pilot, prove value in 2–3 clinics, then scale the network. Interoperability (FHIR) is built in from day one so the system is *GHIMS-exchange-ready* — positioning it for possible public-sector adoption later without requiring government relationships now.

Constraints honored throughout: solo developer + LLM agents; everything on cloud (no on-prem servers); nearby cloud region acceptable for pilot; infra budget < $500/month pre-scale; pilot ASAP.

## 2. Product definition

**Users (personas):**
- **Records/Front desk** — registers patients, verifies identity (Ghana Card / phone), checks in visits, manages queue.
- **Nurse (triage)** — captures vitals, screening, immunizations, ANC contacts.
- **Clinician (doctor / physician assistant / midwife)** — consultation notes, diagnoses (coded), prescriptions, lab orders, referrals.
- **Lab technician** — receives orders, enters results.
- **Pharmacist/dispenser** — dispenses against prescriptions, manages stock.
- **Cashier** — bills visit items, records cash/mobile-money payments, prints receipts, daily reconciliation.
- **Clinic admin** — local users, price list, clinic reports.
- **Network admin (you)** — tenant onboarding, metadata, global reports, support.
- **Patient** — indirect user in v1 (printed summaries, SMS reminders in Phase 4; portal is out of scope until Phase 6).

**Facility profile (v1):** private primary-care clinics and small polyclinics — outpatient-centric, 20–150 visits/day, 1–5 consulting rooms, small lab, dispensary, mostly cash + NHIS-insured patients. Inpatient wards, theatre, and radiology are **out of scope for v1** (Phase 5+ decision point).

**Core value propositions:**
1. Paperless OPD workflow that is *faster* than paper (hard requirement — see KPIs).
2. Patient record follows the patient across the clinic network (with consent).
3. Owner visibility: revenue, morbidity, stock, and staff productivity dashboards.
4. NHIS claims preparation/export (Phase 4) — reduces rejected claims and admin time.
5. No vendor lock-in: open source (MPL 2.0), standard FHIR APIs, data export guaranteed contractually.

**Success criteria for pilot (month ~6):** 3 clinics live; ≥ 90% of OPD visits fully captured digitally; median registration-to-consult data entry overhead ≤ paper baseline; system uptime ≥ 99.5% during clinic hours; 2 clinic owners willing to be references; zero data-loss incidents.

## 3. Locked decisions & rationale (Decision Log)

Agents must treat these as constraints. Changing one requires a new ADR in `/docs/adr/` and human sign-off.

| # | Decision | Rationale | Revisit trigger |
|---|----------|-----------|-----------------|
| D1 | Base platform: **OpenMRS Platform 2.7.x + OpenMRS 3 (O3) frontend**, assembled from the official reference-application distro pattern. Not OpenEMR, not greenfield. | LMIC-proven at national scale (KenyaEMR 2,300+ facilities; UgandaEMR+ national O3 rollout); CIEL concept dictionary; FHIR R4 module; active community. | Only if O3 project stalls upstream (assess yearly). |
| D2 | **Stay close to upstream**: customization via configuration (Initializer metadata-as-code), O3 config schemas, JSON forms, and thin custom modules (`ghanaemr-core`, later `ghanaemr-claims`). No fork of openmrs-core or core ESMs. | Solo maintainability; free upstream security fixes and features; upgrade path. | A required feature is architecturally impossible via module/config (raise ADR first). |
| D3 | **Database: MariaDB 10.11 LTS** for the OpenMRS transactional store. No DB swap, despite Platform 2.7's improving PostgreSQL support — the community module ecosystem is primarily tested on MySQL/MariaDB. **PostgreSQL is used where it's native: the HAPI FHIR shared-record mirror (Phase 5) and analytics.** | Risk containment; each instance only carries clinic-network load, which MariaDB handles easily. | If instance is sharded per-region at >100 clinics and Postgres module support has matured. |
| D4 | **Tenancy: ONE shared cloud OpenMRS instance for the whole network.** Each clinic = an OpenMRS Location subtree. Row-level isolation via the **Data Filter module** (location-based access), plus an explicit consent-based cross-clinic access flow (Section 7). | Single-record-per-patient is the product's core promise; simplest possible ops for a solo operator; cheapest. OpenMRS has no native row multi-tenancy — Data Filter is the community-standard mechanism for multi-site single-server deployments. | >30 clinics, OR a clinic contractually demands hard isolation, OR sustained p95 latency misses targets → shard to instance-groups + central FHIR SHR (design ready in Phase 5). |
| D5 | **Deployment: Docker Compose on cloud VMs** (prod + staging), NOT Kubernetes, for Phases 0–4. | Solo operator; K8s is operational overhead with no payoff below ~dozens of services. Migration path documented (WP-086). | >20 clinics or >2 engineers or instance-sharding begins. |
| D6 | **Cloud region: AWS af-south-1 (Cape Town) as default candidate; final choice by measured latency from Ghanaian clinic links (WP-004).** Azure South Africa North and AWS eu-west-1/2 are the alternates. In-country Ghana hosting is a Phase 5 evaluation (data-sovereignty positioning for any public-sector future). | User accepts nearby region for pilot; LHIMS scandal makes *eventual* in-country hosting strategically valuable. | Public-sector engagement, or DPC guidance requiring localization. |
| D7 | **Connectivity model: online-first.** No offline mode in v1 (accepted consequence of all-cloud decision). Mitigations are operational: dual-SIM 4G failover router + UPS in every clinic kit, aggressive frontend caching, and a paper-fallback + re-entry SOP. | O3 offline tooling is immature; offline sync is the single biggest complexity multiplier. | Pilot telemetry shows >2% of clinic hours with connectivity loss, or CHPS-tier expansion (Phase 6). |
| D8 | **Clinical scope v1:** OPD spine (registration → triage/vitals → consult → coded diagnosis → e-prescription → lab order/result → dispensing → billing/receipt) + immunization + basic ANC. NHIS claims **export** in Phase 4 (no NHIA API access exists today). Inpatient: out of scope. | Primary-care target; ASAP pilot; claims need research + tariff data but not API access. | — |
| D9 | **Identity:** internal auto-generated Network MRN (idgen) is the primary identifier; Ghana Card number, NHIS number, and phone are additional identifiers used for search/matching. No biometric in v1. | Ghana Card is near-universal and already the state's patient-ID direction; NIA verification API requires access we don't have (adapter mocked). | NIA API access obtained (Phase 5 opportunity). |
| D10 | **Licensing & openness:** all our code MPL 2.0 w/ Healthcare Disclaimer (matching OpenMRS); public GitHub monorepo (secrets in a private repo); we operate as a hosted-service company on top of open code. | Credibility vs. LHIMS-style lock-in; community leverage; future MoH pitch. | — |
| D11 | **Payments:** cash + mobile-money *recording* only in v1 (no payment-gateway integration). Gateway (e.g., Hubtel) is Phase 5 optional. | Scope control. | Clinic demand. |
| D12 | **Language:** English UI (Ghana's clinical documentation language). i18n keys discipline from day 1; patient-facing printables get Twi/Ewe/Ga/Dagbani variants later. | Matches clinical reality; cheap to defer. | Pilot feedback. |

## 4. System architecture

### 4.1 Logical view (pilot → Phase 4)

```
                         ┌────────────────────── Cloud region (see D6) ───────────────────────┐
  Clinic A browsers ─┐   │  Cloudflare (DNS, WAF, TLS, caching)                                │
  Clinic B browsers ─┼──▶│   └─ Caddy reverse proxy (VM "app-1")                               │
  Clinic C browsers ─┘   │       ├─ O3 SPA  (static microfrontend assets)                      │
   (each clinic: dual-   │       ├─ OpenMRS Platform 2.7 (Tomcat 9, Java 17*)                  │
    SIM 4G failover      │       │    modules: webservices.rest, fhir2, spa, initializer,      │
    router + UPS)        │       │    idgen, datafilter, addresshierarchy, attachments,        │
                         │       │    reporting(+rest), queue, appointments, stockmanagement,  │
                         │       │    billing(P2), ghanaemr-core, ghanaemr-claims(P4)          │
                         │       └─ MariaDB 10.11 (VM "db-1", private net)                     │
                         │            ├─ nightly mariabackup + streamed binlogs ──▶ S3 (enc.)  │
                         │            └─ (P4) read replica ──▶ Metabase (analytics)            │
                         │  (P5) HAPI FHIR "SHR-mirror" on PostgreSQL ◀── fhir2 sync           │
                         │        = GHIMS-exchange-ready facade + IPS patient summaries        │
  You (admin) ── Tailscale mesh ──▶ SSH, Grafana agent, Uptime Kuma, DB console                │
                         └──────────────────────────────────────────────────────────────────────┘
  *Exact Java/Tomcat versions pinned in WP-001 from official platform docs — do not guess.
```

### 4.2 Key architectural properties
- **Single logical patient record** across all clinics (D4). Data Filter enforces that staff see only patients mapped to their clinic; a consent action at check-in maps a visiting patient to a new clinic (Section 7).
- **Metadata-as-code:** every concept, location, role, form, drug, price list ships as versioned Initializer CSV/JSON in git. A fresh environment must be reproducible from `git clone + docker compose up + initializer run`. This is the #1 enabler for LLM-driven work: config diffs are reviewable text.
- **Two environments** (staging, prod) + local dev. Staging is a smaller VM, refreshed from anonymized prod snapshots (WP-047a).
- **Interoperability-first:** the fhir2 module exposes FHIR R4 from day one; Phase 5 adds an outward-facing HAPI FHIR mirror so external parties (GHIMS exchange, labs, insurers) never touch the transactional core.
- **Blast-radius controls for shared tenancy:** full audit logging, per-clinic report scoping, daily off-site encrypted backups with 15-min binlog RPO, and quarterly restore drills (Section 10).

### 4.3 Scale model & headroom
Load math for D4/D5 sanity: 50 clinics × 100 visits/day ≈ 5,000 visits/day ≈ 8–10 encounters + ~40 obs each → well under 1M rows/day; concurrency ≈ 300–600 active users peak. A single well-tuned 8–16 vCPU Tomcat + MariaDB pair with a read replica for reporting handles this; O3's static frontend offloads to Cloudflare. Verified by load tests at WP-034 (baseline) and WP-080 (50-clinic simulation). Sharding design (instance-groups + central SHR) is pre-written in Phase 5 so growth never forces a panic rewrite.

## 5. Technology stack

**Version policy:** pin exact versions in `VERSIONS.md` during WP-001 by consulting official sources (GitHub releases, OpenMRS docs). The table below fixes the *choice*; the agent must never invent a version number or API — verify, then pin.

| Layer | Choice | Notes |
|---|---|---|
| EMR platform | OpenMRS Platform 2.7.x (Java/Spring/Hibernate, Tomcat 9) | Latest stable 2.7 patch at WP-001 |
| Frontend | OpenMRS 3 microfrontends (React 18 + TypeScript, single-spa/module federation via openmrs app shell) | Start from `openmrs/openmrs-distro-referenceapplication` assembly |
| Forms | O3 React Form Engine (JSON form schemas) + Form Builder app for authoring | Forms live as JSON in repo, loaded by Initializer |
| Terminology | CIEL dictionary (via packaged release import; OCL subscription optional later) + ICD-10 mappings included in CIEL | WP-014 |
| Config-as-code | `openmrs-module-initializer` (Mekom) | All metadata in `distro/configs/` |
| Multi-clinic isolation | `openmrs-module-datafilter` | Location-based row filtering |
| IDs | `openmrs-module-idgen` (Network MRN) + custom Ghana Card validator in `ghanaemr-core` | |
| Address data | `openmrs-module-addresshierarchy` + Ghana regions/districts CSV (16 regions, 261 MMDAs — verify current count from Ghana Statistical Service at WP-013) | |
| APIs | `webservices.rest` (primary app API) + `fhir2` (FHIR R4) | REST docs: rest.openmrs.org |
| Queues/appointments | `openmrs-module-queue` + esm-service-queues; Bahmni appointments backend + esm-appointments (Phase 2) | |
| Pharmacy/stock | esm-dispensing-app + `openmrs-module-stockmanagement` (METS/UgandaEMR lineage) | |
| Billing | Evaluate `openmrs-module-billing` + esm-billing (KenyaEMR/Palladium lineage) first; fallback = minimal custom `ghanaemr-billing` (schema in WP-041) | Decision spike WP-040 |
| Attachments | `attachments` module (patient photos, scanned docs) | Storage on encrypted volume; S3 offload later |
| Reporting (operational) | `reporting` + `reportingrest` modules with SQL dataset definitions | WP-030 |
| Analytics (owner dashboards) | Metabase on MariaDB read replica (Phase 4) | Superset only if Metabase limits hit |
| Custom backend | `ghanaemr-core` (Maven OMOD): Ghana Card checksum validator, consent/access service, NHIS entities (P4), REST resources | Java, JUnit tests mandatory |
| Custom frontend | `esm-ghana-*` packages only where config can't do it (network patient search + consent UI; claims UI in P4) | TypeScript, follow O3 ESM conventions |
| Transactional DB | MariaDB 10.11 LTS | utf8mb4; tuned in WP-044 |
| FHIR mirror / SHR-lite (P5) | HAPI FHIR JPA server on PostgreSQL 16 | The external-facing, GHIMS-ready facade |
| Reverse proxy / TLS | Caddy (auto-TLS) behind Cloudflare | |
| Containers | Docker + Docker Compose v2, images pinned by digest, published to GHCR | |
| Hosts | Ubuntu 24.04 LTS VMs; `app-1` (8 vCPU/32 GB) + `db-1` (4 vCPU/16 GB, or co-located to start) + `staging-1` (4/8) | Right-size at WP-044 |
| Admin access | Tailscale (SSH + internal dashboards only over tailnet; no public admin ports) | |
| Secrets | SOPS + age; encrypted files in private `sankofa-secrets` repo; injected via env files at deploy | |
| Backups | mariabackup nightly + binlog streaming → S3-compatible bucket (server-side + client-side encryption), 35-day retention; weekly restore verification job | RPO ≤ 15 min, RTO ≤ 4 h |
| Monitoring | Grafana Cloud free tier (node/JVM/MariaDB metrics + logs via Alloy), Uptime Kuma (public status page), Sentry free tier (frontend + backend errors) | Alert runbooks WP-045 |
| CI/CD | GitHub Actions: lint → unit tests → build OMODs/ESMs → docker build → push GHCR → auto-deploy staging → manual-gate deploy prod (SSH + `compose pull && up -d`) | |
| E2E / load | Playwright (reuse O3 upstream patterns) + k6 | |
| SMS (P4) | Hubtel or Africa's Talking (both serve Ghana) behind a provider-agnostic adapter | |
| Docs | `/docs` in repo (mkdocs-material site, Phase 5 publish) | |

## 6. Clinical content & data design

### 6.1 Identifiers
| Identifier | Type | Rules |
|---|---|---|
| Network MRN (primary) | idgen auto | Format `SNK-NNNNNN-C` (mod-30 check digit), clinic-agnostic — one MRN per human across the network |
| Ghana Card PIN | manual | Format `GHA-#########-#` (regex `^GHA-\d{9}-\d$`); checksum algorithm to be confirmed against NIA documentation in WP-012 — implement regex first, checksum behind a flag |
| NHIS membership no. | manual | 8-digit numeric (verify current format vs. NHIS cards in WP-012); required later for claims |
| Phone (MSISDN) | person attribute | Normalized to +233 E.164; used in duplicate search |

Duplicate prevention (WP-017): registration search-before-create by Ghana Card → phone → name+DOB+sex fuzzy; block exact Ghana Card duplicates; merge tooling in WP-075.

### 6.2 Location & metadata model
`Sankofa Network` → `Clinic X` → departments (`OPD`, `Triage`, `Lab`, `Pharmacy`, `Cashier`, `ANC`). Location tags: Login Location, Visit Location, Dispensing Location. Visit types: `OPD`, `ANC`, `Immunization`, `Pharmacy-only`. Encounter types: `Registration`, `Triage`, `Consultation`, `Lab Order`, `Lab Result`, `Dispensing`, `Immunization`, `ANC Contact`, `Referral`, `Billing`. Roles map 1:1 to personas in Section 2 with a written privilege matrix (WP-011 output, kept as CSV).

### 6.3 Forms (v1 — all as React Form Engine JSON, concepts from CIEL)
1. **Triage/Vitals** — temp, pulse, RR, BP, SpO2, weight, height (auto-BMI), MUAC (<5s), pregnancy status, chief complaint.
2. **OPD Consultation** — history, exam, malaria RDT section (result + ACT treatment prompt), coded diagnoses (multi, CIEL/ICD-10), plan, orders launcher, disposition (home/review/refer/admit-elsewhere).
3. **Lab Request & Result** — v1 catalog: malaria RDT/microscopy, FBC, Hb, blood glucose (RBS/FBS), urine R/E, pregnancy test (urine hCG), typhoid (Widal), HIV rapid (with restricted-visibility handling — WP-016), Hep B sAg, blood group, stool R/E. Specimen, result values with units + ranges, technician sign-off.
4. **ANC First Contact + Follow-up** — aligned to Ghana's adaptation of the WHO 8-contact model (obtain current GHS ANC register/guideline as source material in WP-023): LMP/EDD calc, gravida/para, TT/Td doses, IPTp-SP doses, Hb, syphilis/HIV screening links, danger signs, birth-prep plan.
5. **Immunization** — O3 immunization widget configured with the current Ghana EPI schedule (obtain from GHS/EPI in WP-024; typical antigens BCG, OPV, Penta, PCV, Rota, MR, Men A, YF, plus vitamin A — verify doses/ages, do not hardcode from memory).
6. **Referral note** — destination, reason, summary; printable.

### 6.4 Medicines & pricing
Drug list seeded from the **Ghana Essential Medicines List** (current edition obtained in WP-015) as Initializer `drugs.csv` with CIEL concept mappings, forms, strengths. Each clinic gets a **price list** (service + drug prices) as CSV → billing (WP-041). NHIS Medicines List + G-DRG tariffs are acquired in Phase 4 (WP-070) for claims mapping.

### 6.5 Printables (WP-028)
Prescription, receipt (80mm thermal, ESC/POS-friendly via browser print CSS), lab report, referral letter, immunization card, patient visit summary. All carry clinic letterhead (per-location branding config).

### 6.6 Operational reports v1 (WP-030)
OPD register (GHS-format-compatible columns), daily cash summary per clinic, top-morbidity table (weekly/monthly), visit counts by clinic/day, immunization tally, ANC register, stock consumption. All exportable CSV; scoped by Data Filter so clinic admins see only their clinic.

## 7. Multi-clinic access & consent model (core custom logic)

**Default:** on registration, the patient is *mapped* (Data Filter entity–basis mapping) to the registering clinic. Staff only ever query/see mapped patients.

**Cross-clinic visit flow (WP-022):**
1. Front desk at Clinic B searches locally → not found → clicks **"Search network"** (custom ESM action). Network search returns *limited demographics only* (name, age, sex, masked MRN/phone) — enough to confirm identity, not clinical data.
2. Staff selects match → **consent capture screen**: patient (or guardian) consents to Clinic B accessing their record; recorded as an obs/encounter with staff attestation checkbox (+ optional consent-form photo via attachments). Emergency-override path exists ("break-the-glass") requiring a reason, flagged in audit and reported weekly to network admin.
3. On consent save, `ghanaemr-core` service adds the patient→Clinic B Data Filter mapping and writes an immutable audit entry. Record is now fully visible at Clinic B.
4. Patients can revoke via any clinic admin (mapping end-dated; history of the access retained for audit).

**Privacy invariants (test-enforced, WP-021/022):** a user with only Clinic A roles can never read Clinic B-only patients via UI, REST, *or* FHIR; network search endpoint returns no clinical payloads; every mapping change is audited with actor, patient, clinic, reason, timestamp. HIV rapid-test results carry an additional privilege gate (view-restricted concept class) per WP-016.

## 8. Integrations & interoperability

**Now (no external access available):** everything behind a **pluggable adapter layer** in `ghanaemr-core` with mock implementations + contract tests:
- `IdentityVerifier` (NIA Ghana Card verification) → `MockIdentityVerifier` (format/checksum only).
- `InsuranceEligibility` (NHIA membership check) → mock returning "unverified — capture manually".
- `SmsGateway` (P4) → console/mock, then Hubtel/Africa's Talking implementations.
- `ClaimsSubmitter` (P4) → file-export implementation (XLSX/PDF/XML per WP-070 findings); API implementation slot reserved.

**NHIS claims design (Phase 4, WP-070/071):** capture NHIS membership + eligibility evidence at check-in; assemble claim per visit from diagnoses (ICD-10 → G-DRG grouping via a maintained mapping table), procedures, and NHIS-list medicines; claims-review queue UI (`esm-ghana-claims`) with validation errors before export; batch export in whatever format private accredited facilities currently submit (research task — likely NHIA e-claims portal upload; confirm in WP-070); track submitted/paid/rejected manually with reasons, feeding a "rejection-rate" KPI.

**GHIMS-exchange readiness (Phase 5):** stand up HAPI FHIR mirror (Postgres) fed by fhir2; expose Patient, Encounter, Condition, Observation, MedicationRequest, Immunization + an **International Patient Summary (IPS)** endpoint; OAuth2 client-credentials security. When/if GHIMS publishes connection specs, we implement an adapter at the mirror — the transactional EMR never changes. OpenHIM is evaluated (WP-082) only when a real external counterparty exists.

**DHIS2/DHIMS2:** private clinics also report aggregates to GHS. Phase 5 nice-to-have (WP-087 scope): generate DHIMS2-compatible aggregate exports (ADX/CSV) from our reports — a strong selling point to clinics and a credibility card for MoH conversations.

## 9. Security, privacy & compliance (Ghana Data Protection Act, 2012 — Act 843)

**Governance setup (start Week 1 — WP-005, human-led, LLM-drafted):**
- Register with the **Ghana Data Protection Commission (DPC)** as a data processor (each clinic is the data controller for its patients; the network operator processes on their behalf). Registration is legally required and can be slow — file early. Verify current DPC procedure/fees on dataprotection.org.gh during WP-005.
- Designate a Data Protection Supervisor/Officer (you, initially). Draft: privacy notice, patient consent language (registration + cross-clinic sharing + SMS), clinic **Data Processing Agreement** annexed to the service agreement, retention policy (health records retained per Ghana health-records norms — confirm current GHS/MoH retention guidance in WP-005; default: retain, never silently delete), breach-response runbook (contain → assess → notify DPC and affected individuals per Act 843 requirements — verify current notification timelines with DPC guidance), and data-subject-rights procedure (access/correction requests via clinic).

**Technical controls (implemented across WP-046 and infra WPs):**
- TLS everywhere (Cloudflare full-strict + Caddy); HSTS; modern ciphers only.
- Encryption at rest: encrypted VM volumes; client-side-encrypted backups (age) in S3 with object lock; secrets via SOPS/age only — no plaintext secrets in repo/CI logs (CI secret-scanning gate).
- AuthN/AuthZ: OpenMRS RBAC per privilege matrix; strong password policy + account lockout; per-human named accounts only (no shared logins — contractual + training point); session idle timeout 15 min; admin/superuser accounts usable only from tailnet-restricted origin (enforced at proxy). Native 2FA is absent in OpenMRS — compensating controls now; Keycloak SSO+MFA is the Phase 5 fix (WP-083).
- Audit: choose and implement the audit approach in WP-046 (evaluate current community audit-log options vs. a thin Hibernate-interceptor audit in `ghanaemr-core` writing who/what/when/where for reads of restricted classes and all writes); Data Filter mapping changes and break-the-glass events always audited (Section 7).
- Network: only 443 public; SSH and internal dashboards via Tailscale; DB on private interface only; UFW default-deny; unattended-upgrades on.
- AppSec: dependency + image scanning (Trivy) in CI; OWASP-header pass at proxy; rate limiting via Cloudflare; upload type/size limits on attachments; anonymized data only in staging (WP-047a scrubber).
- Verification cadence: restore drill quarterly; access review quarterly; external security assessment before scale (WP-085).

## 10. Infrastructure, environments & operations

**Repo-defined infra:** `/infra` contains cloud-init for each host, `docker-compose.prod.yml`, `docker-compose.staging.yml`, Caddyfile, backup scripts, and `runbooks/` (deploy, rollback, restore, incident, onboarding-a-clinic, offboarding). Every manual action performed twice must become a script or runbook.

**Environments:**
| Env | Purpose | Notes |
|---|---|---|
| local | dev | `docker compose -f compose.dev.yml up` gives full stack + seed data (WP-031) |
| staging | integration, UAT, training sandbox | auto-deployed on merge to `main`; anonymized prod-shaped data |
| prod | live clinics | manual-gate deploy; maintenance window Sun 21:00–23:00 GMT; blue/green via second compose project if zero-downtime needed (documented WP-044) |

**Backup/DR (WP-009 → WP-047):** nightly full (mariabackup) + continuous binlog ship; 35-day retention; monthly *timed* restore drill to a scratch VM with checksum + row-count verification, results logged. Attachments volume rsync'd to S3 nightly. RPO ≤ 15 min / RTO ≤ 4 h are tested claims, not aspirations. Single-region acceptance for pilot; cross-region backup copy enabled from day 1 (cheap insurance).

**Monitoring & alerting (WP-045):** Grafana Cloud dashboards (host, JVM heap/GC, Tomcat threads, MariaDB, HTTP p95, disk); alerts → your phone (email+push): uptime, disk >80%, backup-failed, error-rate spike, cert expiry. Uptime Kuma public status page for clinics. Sentry release-tagged error tracking. Weekly ops review checklist (15 min).

**Capacity guardrails:** alarm at 70% sustained CPU or 75% heap; vertical scale first (resize VM), replica for reporting second, shard per D4 trigger last.

## 11. Engineering standards, repo layout, CI/CD, testing

**Monorepo `sankofa-ehr` (public):**
```
/distro                  # the product assembly
  /configs               # Initializer metadata-as-code (CSV/JSON) — the heart of the product
  /frontend              # O3 assembly: spa-assemble config, import map, branding, esm config JSON
  /openmrs               # distro.properties / module list, Dockerfiles
  compose.dev.yml
/backend
  /ghanaemr-core         # Maven OMOD (validators, consent service, adapters, REST resources)
  /ghanaemr-claims       # Phase 4 OMOD
/frontend-esms
  /esm-ghana-patient-search   # network search + consent UI
  /esm-ghana-claims           # Phase 4
/infra                   # cloud-init, compose files, Caddy, backup, runbooks
/qa                      # Playwright e2e, k6 load, seed-data generators
/docs                    # this plan, ADRs (/docs/adr), VERSIONS.md, privilege-matrix.csv,
                         # compliance/, training/, runbooks index
/work-packages           # WP tracker: one md file per WP with status + links to PRs
```
Private repo `sankofa-secrets` (SOPS-encrypted env files, keys) — never referenced by content in the public repo.

**Standards:** Conventional Commits; PR-per-WP (small WPs) or PR-per-task (large WPs); ADR for any deviation from Section 3; CHANGELOG kept; Java: Spotless+Checkstyle, JUnit ≥ 80% line coverage on `ghanaemr-*` service code; TS: ESLint+Prettier, unit tests for logic-bearing components; every WP updates docs it touches ("docs-or-it-didn't-happen").

**CI/CD pipeline (WP-008):** on PR → lint, unit tests, build OMOD + ESMs, Trivy scan, secret scan, docker build. On merge to main → push images (digest-pinned) to GHCR, deploy staging, run Playwright smoke suite against staging, notify. Prod deploy = manual workflow dispatch with version tag + automatic pre-deploy DB snapshot + one-command rollback (previous digest).

**Test strategy:**
- Unit (Java/TS) — logic, validators, consent service.
- API contract — REST/FHIR assertions incl. the Section 7 privacy invariants as *permanent* regression tests (two-user isolation suite).
- E2E (Playwright, WP-033) — golden path: register → triage → consult → prescribe → lab → dispense → bill → report; runs nightly vs staging and pre-prod-deploy.
- Load (k6) — WP-034 baseline (login, patient search p95 < 800 ms, encounter save p95 < 1.5 s at pilot concurrency ×3), WP-080 50-clinic profile.
- Restore drills count as tests. A WP is not done until its acceptance criteria are demonstrated by an automated test or a logged drill.

## 12. Roadmap & timeline

Assumes ~full-time solo + agents. Buffers are real: clinic scheduling, hardware shipping, and DPC paperwork are the usual slippers, not code.

| Phase | Calendar | Theme | Exit criteria |
|---|---|---|---|
| 0 — Foundations | Weeks 1–2 | Accounts, repo, stock O3 running, staging VM, CI, backups v0, legal filings started, region latency test | Stock O3 reachable on staging over HTTPS; CI green; DPC application drafted |
| 1 — Core distro | Weeks 3–10 | Metadata, forms, isolation + consent, OPD clinical spine, dispensing, stock, reports, e2e suite | Demo: full OPD visit for two clinics with proven isolation + consent flow; e2e green |
| 2 — Pilot-ready | Weeks 11–16 | Billing + queues + appointments, prod env, monitoring, hardening, training pack, onboarding kit, UAT with clinic 1 | UAT sign-off; prod deployed; restore drill passed; training materials done |
| 3 — Pilot | Weeks 17–26 | Go-live clinic 1, then 2 & 3 (staggered ~2–3 wks); hypercare; iteration buffer | Success criteria in Section 2 measured; post-pilot report |
| 4 — NHIS & growth | Months 7–9 | NHIS claims export, SMS reminders, Metabase, data import, merge tooling, onboarding automation | First real claims batch exported & accepted; clinic-onboarding ≤ 2 days effort |
| 5 — Scale & interop | Months 10–14 | 50-clinic load proof, HAPI FHIR mirror + IPS, Keycloak MFA, security assessment, in-country hosting eval, MoH/GHS engagement pack, public docs/demo | Load test passed; FHIR facade live; audit fixes closed; capability statement published |
| 6 — Horizon (unscheduled) | Year 2 | Patient portal, offline/CHPS mobile (Android FHIR SDK/OpenSRP-style), regional sharding + true SHR, payment gateway, national pitch | — |

## 13. Work package catalog

### 13.1 WP template (use verbatim for every WP file in `/work-packages`)
```
ID / Title / Phase / Status
Depends on: [WP ids]
Goal: one sentence.
Context to load: [files/dirs in repo, doc sections, external doc URLs]
Tasks: numbered, concrete steps.
Out of scope: explicit exclusions.
Acceptance criteria: checkable statements (each maps to a test, artifact, or logged drill).
Test plan: which automated tests prove the ACs.
Artifacts: code paths, config paths, docs to update.
Estimate: S (<½ day) / M (~1 day) / L (2–4 days) — agent-assisted.
```

### 13.2 Two fully expanded exemplars (pattern for the agent)

---
**WP-012 — Identifier types & Ghana Card validation** · Phase 1 · Depends: WP-010, WP-011, WP-020
**Goal:** All four identifiers from §6.1 exist as metadata; Network MRN auto-generates; Ghana Card numbers are format-validated at registration.
**Context to load:** §6.1; `distro/configs/` Initializer docs (patientidentifiertypes, idgen domains — read the Initializer README for exact CSV headers, do not guess); `backend/ghanaemr-core`.
**Tasks:** (1) Initializer CSVs for the 4 identifier types (Ghana Card, NHIS, phone-as-attribute per O3 registration convention — check O3 registration config docs for attribute vs identifier handling of phone). (2) idgen source for MRN `SNK-` prefix, 6 digits, mod-30 check, autogeneration on registration. (3) In `ghanaemr-core`, a `PatientIdentifierValidator`-conforming Ghana Card validator: regex `^GHA-\d{9}-\d$`; checksum verification stubbed behind config flag `ghanaemr.ghanacard.checksum=off` with TODO referencing NIA spec acquisition. (4) Wire validator to the identifier type. (5) Registration form config: Ghana Card + NHIS + phone fields, MRN read-only. (6) Unit tests: 10 valid/invalid PIN cases; idgen check-digit tests. (7) Update `docs/identifiers.md`.
**Out of scope:** NIA online verification (adapter mocked per §8); duplicate-merge tooling (WP-075).
**Acceptance criteria:** registering without Ghana Card succeeds (optional field); malformed PIN blocks save with clear message; MRN auto-assigned and unique across two test clinics; all metadata reproducible on a clean `compose up` (CI job proves it); unit tests green.
**Test plan:** JUnit validator tests; Playwright registration test (valid + invalid PIN); CI clean-environment metadata-load job.
**Artifacts:** `distro/configs/{patientidentifiertypes,idgen,attributetypes}/…`, `backend/ghanaemr-core/src/...Validator.java`, `docs/identifiers.md`.
**Estimate:** M.

---
**WP-022 — Cross-clinic network search + consent flow** · Phase 1 · Depends: WP-017, WP-020, WP-021
**Goal:** Implement §7 exactly: privacy-preserving network patient search, consent capture, Data Filter mapping grant, break-the-glass, audit.
**Context to load:** §7; Data Filter module docs/README (entity-basis mapping API); O3 extension-slot docs for adding actions to patient-search UI; `esm-ghana-patient-search` scaffold.
**Tasks:** (1) `ghanaemr-core`: REST resource `GET /ws/rest/v1/ghanaemr/networksearch?q=` returning limited demographics (name, age band, sex, masked phone last-3, MRN last-3) for patients *not* mapped to caller's clinic; requires new privilege `App: ghanaemr.networkSearch`. (2) REST resource `POST /ghanaemr/accessgrant` {patient, clinic, mode: consent|emergency, reason?, consentEncounterUuid} → creates Data Filter mapping via module API + audit row (custom table `ghanaemr_access_audit`: actor, patient, clinic, mode, reason, timestamp — Liquibase changeset). (3) Consent encounter type + mini-form (attestation checkbox, optional consent-photo attachment). (4) `esm-ghana-patient-search`: "Search network" action on empty local results → results list → consent modal → grant → route to patient chart. Emergency path requires reason text, shows warning banner on chart for that session. (5) Weekly break-the-glass report added to WP-030 report set. (6) Contract tests: the two-user isolation suite (WP-021) extended — Clinic B user cannot read Clinic A patient via REST/FHIR before grant, can after, cannot after revoke; network search never returns clinical fields (schema-asserted). (7) Docs: `docs/consent-model.md` + training snippet.
**Out of scope:** patient-initiated revocation UI (admin-mediated per §7); biometric identity.
**Acceptance criteria:** all §7 privacy invariants pass as automated tests; grant/revoke round-trip works in UI; every grant produces an audit row; emergency grants appear in weekly report; no clinical data in network-search payload (test-asserted).
**Test plan:** JUnit service tests; REST contract suite (two clinics, three users); Playwright consent-flow e2e; FHIR isolation probe.
**Artifacts:** `backend/ghanaemr-core/...`, `frontend-esms/esm-ghana-patient-search/`, `distro/configs/encountertypes`, `docs/consent-model.md`.
**Estimate:** L (the most sensitive WP in the project — human review of the diff is mandatory).

---

### 13.3 Catalog — Phase 0 (Weeks 1–2)
- **WP-000 Bootstrap** — GitHub org, public monorepo skeleton per §11, MPL-2.0+HD license, README, ADR template, WP tracker files, issue/PR templates. *AC:* repo builds empty CI green.
- **WP-001 Version pinning & stock spin-up** *(dep 000)* — From official OpenMRS sources, pin Platform 2.7.x patch, O3 frontend release tags, all §5 module versions, Java/Tomcat/MariaDB versions → `VERSIONS.md` with source links; stand up the unmodified reference-application distro locally via Docker. *AC:* `compose up` → login works; every version cites a URL; NO version numbers invented.
- **WP-002 Dev environment & Makefile** *(dep 001)* — `make dev/test/build/seed`; contributor quickstart doc. *AC:* clean-machine 30-min setup validated.
- **WP-003 Accounts & secrets base** — Domain (+ `.gh` variant check), Cloudflare, GHCR, S3 bucket, Sentry, Grafana Cloud, Tailscale tailnet; SOPS/age keys; `sankofa-secrets` repo. *AC:* secrets round-trip documented; no plaintext secrets anywhere (CI scanner on).
- **WP-004 Region selection harness** — Script measuring latency/throughput from Ghanaian vantage points (clinic links when available; Ghana-based VPS/RIPE Atlas otherwise) to af-south-1, eu-west-1/2, Azure ZA-North; ADR-001 records the pick per D6. *AC:* data table + signed ADR.
- **WP-005 Compliance kickoff (human+agent)** — DPC processor-registration research + drafted application; privacy notice v0; consent texts v0; clinic DPA template v0; breach runbook v0; retention-policy memo with verified current DPC/GHS guidance links. *AC:* filing-ready pack reviewed by you (and ideally a Ghanaian lawyer — budget line exists).
- **WP-006 Brand & naming** — Name availability sweep (trademark registry, domains, Ghana company names), logo placeholder, O3 theming tokens, favicon. *AC:* branding config renders on login page.
- **WP-007 Staging host** *(dep 003)* — Cloud-init provision `staging-1`: Docker, Caddy, Tailscale, UFW default-deny (443 public only), unattended-upgrades; deploy stock distro. *AC:* HTTPS reachable; SSH only via tailnet; hardening checklist logged.
- **WP-008 CI/CD skeleton** *(dep 001,007)* — Pipeline per §11 incl. staging auto-deploy + Playwright smoke placeholder. *AC:* merge→staging fully automatic; rollback runbook tested once.
- **WP-009 Backups v0** *(dep 007)* — mariabackup nightly + binlog ship to S3 (encrypted), restore script; first restore drill logged. *AC:* drill restores staging to ≤15-min-old state on scratch container.

### 13.4 Catalog — Phase 1 (Weeks 3–10) · Core distro
- **WP-010 Distro assembly** *(dep 001)* — Fork/derive reference-application assembly: backend module set per §5 (excl. Phase-2/4 modules), SPA build with pinned ESMs, empty `distro/configs` wiring, dev/staging images. *AC:* our distro (not stock) on staging; module list = `VERSIONS.md`.
- **WP-011 Metadata baseline** *(dep 010)* — Locations tree (2 demo clinics + depts), tags, visit/encounter types, role & privilege matrix CSV per §6.2, seed users. *AC:* clean-env reproducibility CI job; matrix doc committed.
- **WP-012 Identifiers** — expanded above.
- **WP-013 Ghana address hierarchy** *(dep 010)* — Regions/districts CSV sourced from Ghana Statistical Service (record source+date), addresshierarchy config, registration address fields. *AC:* cascading region→district selection works; source cited.
- **WP-014 CIEL load** *(dep 010)* — Import packaged CIEL release (record release date); smoke-verify key concept UUIDs used by our forms; document update procedure. *AC:* concept counts sane; forms' concept references resolve; deterministic re-import.
- **WP-015 Ghana drug list** *(dep 014)* — Obtain current Ghana EML (cite edition); build `drugs.csv` with CIEL maps, forms, strengths; load. *AC:* prescriber search finds EML drugs w/ correct strengths; source doc archived in `/docs/sources`.
- **WP-016 Lab catalog** *(dep 014)* — Orderables + specimen types + result concepts with units/ranges for §6.3 list; HIV-result restricted-visibility privilege gate. *AC:* order→result round-trip for 3 test types; HIV result hidden from Cashier role (test).
- **WP-017 Registration config** *(dep 012,013)* — Field set per §6.1/6.3; search-before-create (GhanaCard→phone→fuzzy); exact-GhanaCard duplicate block. *AC:* Playwright covers create, dup-block, search paths.
- **WP-018 Triage/vitals** *(dep 014)* — Vitals+MUAC config, auto-BMI, abnormal-value flags. *AC:* values render on chart with flags.
- **WP-019 OPD consult form** *(dep 014,016)* — §6.3 form #2 as Form Engine JSON incl. malaria RDT block + coded multi-diagnosis + disposition. *AC:* completed consult renders in visit summary; diagnoses coded (ICD-10 mapping present); e2e updated.
- **WP-020 ghanaemr-core scaffold** *(dep 001)* — Maven OMOD skeleton, activator, config flags pattern, adapter interfaces per §8 with mocks + contract tests, CI wiring. *AC:* module loads on platform; sample REST ping resource; ≥80% coverage on service code from the start.
- **WP-021 Multi-clinic isolation** *(dep 011,020)* — Data Filter enablement, location entity-basis config, auto-map-on-registration hook, the permanent two-user isolation test suite (UI/REST/FHIR). *AC:* isolation suite green and wired as required CI check forever.
- **WP-022 Network search + consent** — expanded above.
- **WP-023 ANC forms** *(dep 014)* — First + follow-up per §6.3 #4 with sourced GHS materials; EDD calc; danger-sign highlighting. *AC:* midwife scenario e2e; source docs archived.
- **WP-024 Immunization** *(dep 014)* — O3 immunization config with verified current Ghana EPI schedule (source archived); card printable. *AC:* child scenario: 2 antigens recorded, card prints, due-next logic sane.
- **WP-025 Dispensing** *(dep 015,019)* — esm-dispensing flow: pending prescriptions → dispense (full/partial) → label/receipt data handoff. *AC:* prescription→dispense e2e; partials handled.
- **WP-026 Lab results entry** *(dep 016)* — Orders worklist + result entry + clinician notification badge + printable report. *AC:* order→result→print e2e.
- **WP-027 Attachments** *(dep 010)* — Enable module; type/size limits; encrypted volume; nightly S3 sync. *AC:* upload/view photo + PDF; limits enforced; restore drill includes attachments.
- **WP-028 Printables** *(dep 019,025,026)* — §6.5 set incl. 80mm receipt CSS; per-clinic letterhead from location attributes. *AC:* visual print checks on thermal + A4 logged.
- **WP-029 Theming & session polish** *(dep 006,010)* — Branding, login-location behavior, 15-min idle timeout, friendly error pages. *AC:* UX checklist pass.
- **WP-030 Reports v1** *(dep 019–026)* — §6.6 SQL dataset definitions + report page + CSV export + Data-Filter scoping + break-the-glass weekly report. *AC:* clinic admin sees own-clinic-only numbers (test); network admin sees all; figures reconcile against seeded fixtures.
- **WP-031 Seed & demo data** *(dep 017–026)* — Deterministic synthetic generator (Ghanaian name lists, realistic morbidity mix) for staging/demos + Playwright fixtures. *AC:* `make seed` idempotent; demo script doc.
- **WP-032 Stock management** *(dep 015,025)* — Per-clinic pharmacy stores, receipts/adjustments, dispense-decrement link, reorder-level report. *AC:* stock ledger matches dispensing e2e; negative-stock blocked.
- **WP-033 E2E suite** *(dep most above)* — Golden-path Playwright per §11 + consent-flow + isolation UI checks; nightly vs staging. *AC:* suite < 15 min, green 3 consecutive nights.
- **WP-034 Perf baseline** *(dep 033)* — k6 scripts + thresholds per §11; document current p95s. *AC:* thresholds codified in CI perf job (non-blocking initially).

### 13.5 Catalog — Phase 2 (Weeks 11–16) · Pilot-ready
- **WP-040 Billing decision spike** *(dep 010)* — Evaluate community billing module + esm-billing against our platform pins on a branch; timeboxed 3 days; ADR-002 chooses path A (adopt+configure) or B (minimal `ghanaemr-billing`: tables `price_list_item(clinic, item_type[svc|drug|lab], concept/drug ref, price, active)`, `invoice(visit, status)`, `invoice_line`, `payment(method[cash|momo], amount, ref)`; auto-line-items from orders/dispensing; void-with-reason). *AC:* ADR signed; chosen path's schema/config documented.
- **WP-041 Billing implementation** *(dep 040)* — Implement chosen path: per-clinic price lists (CSV via Initializer or import script), cashier screen, invoice from visit, cash/MoMo payment recording, receipt print, void flow, daily cash summary feeding WP-030. *AC:* full billed-visit e2e; cash summary reconciles to payments table exactly; price update procedure documented.
- **WP-042 Service queues** *(dep 019,041)* — Queue module config: registration→triage→consult→lab→pharmacy→cashier boards per clinic; wall-display mode. *AC:* patient visibly moves through queue in e2e; average-wait metric captured.
- **WP-043 Appointments** *(dep 010)* — Appointments backend+ESM config; follow-up booking from consult disposition; daily list report. *AC:* book/reschedule/honor flow e2e.
- **WP-044 Production environment** *(dep 007–009)* — Provision `app-1`+`db-1` per §5 sizing (ADR if co-locating first); prod compose profile; MariaDB tuning (buffer pool, slow-query log); DNS cutover plan; blue/green procedure doc; prod backups per §10. *AC:* prod serves distro; restore-to-prod drill passed; tuning values recorded.
- **WP-045 Observability** *(dep 044)* — Grafana Cloud agents (host/JVM/MariaDB/HTTP), dashboards, alert rules + phone routing, Uptime Kuma public page, Sentry wired with release tags; runbook per alert. *AC:* synthetic failure of each alert fires within SLA; runbooks linked from alerts.
- **WP-046 Security hardening pass** *(dep 020,044)* — Implement §9 technical-controls list incl. audit-approach decision+implementation (ADR-003), password/lockout policy, proxy-enforced tailnet-only superuser, Trivy gates, OWASP headers, Cloudflare rate rules. *AC:* documented checklist 100%; audit rows verified for restricted reads + all mapping changes; scanner zero criticals.
- **WP-047 DR finalization** *(dep 044)* — Prod RPO/RTO drill with evidence; cross-region backup copy verified; quarterly-drill calendar automation. **WP-047a Staging anonymizer** — deterministic scrubber (names→synthetic, contacts→fake, GhanaCard→invalidated pattern) for prod→staging refreshes; verification queries prove no real PII. *AC:* drill report; anonymizer test suite green.
- **WP-048 Training pack** *(dep 033,041,042)* — Role-based quick guides (1–2 pages each, screenshot-rich PDFs), 2-day on-site curriculum, competency checklist, sandbox tenant on staging, short screen-recordings for the 6 core flows. *AC:* a non-author can complete each role's tasks using only the guide (tested with 1 volunteer).
- **WP-049 Clinic onboarding kit** — Hardware BOM (§14.1), network setup guide (dual-SIM router config), room-flow placement advice, printer setup (thermal + A4), go-live checklist, paper-fallback + re-entry SOP, physical signage (privacy poster). *AC:* kit assembled once end-to-end and photographed for the runbook.
- **WP-050 UAT with Clinic 1** *(dep 041–049)* — Scripted UAT on staging sandbox with real clinic staff (2 sessions); log issues; fix P1/P2. *AC:* clinic-owner sign-off memo; issue list triaged to zero P1.
- **WP-051 Data import tool** — XLSX patient-demographics template + REST importer with row-level validation report + dry-run mode. *AC:* 1k-row import <10 min with correct MRN/duplicate handling; report readable by clinic admin.
- **WP-052 Legal pack final** *(dep 005)* — Service agreement + DPA + consent texts + privacy poster finalized (lawyer-reviewed budget line); e-sign flow (PandaDoc/Docuseal or paper). *AC:* Clinic 1 contract executed.

### 13.6 Catalog — Phase 3 (Weeks 17–26) · Pilot
- **WP-060 Go-live Clinic 1** — Cutover plan (hardware install day, data import, users, price list, dry-run morning, go-live), you on-site week 1, daily hypercare check-ins weeks 1–2, decision log. *AC:* ≥90% of visits digital by end of week 2; issues logged/triaged.
- **WP-061 Support system** — WhatsApp support line + lightweight ticket log (GitHub Issues private mirror or simple tracker), severity SLAs (P1 response 2h clinic-hours / fix 8h; P2 next-day), status-page comms procedure. *AC:* SLA doc shared with clinics; first 10 tickets resolved within SLA.
- **WP-062 Go-live Clinics 2–3** *(dep 060 learnings)* — Staggered 2–3 weeks apart using updated runbook; first **network patient** (consent flow used in anger) validated. *AC:* onboarding effort per clinic measured (target ≤ 5 person-days incl. training).
- **WP-063 Pilot metrics** *(dep 045)* — KPI dashboard (§18) auto-collected: digital-capture %, uptime clinic-hours, p95s, consent events, error rates; weekly review ritual doc. *AC:* dashboard live; 4 weekly reviews held.
- **WP-064 Iteration buffer** — 2 reserved weeks: top pilot pain-points → mini-WPs, prioritized with clinic owners. *AC:* before/after metric for each fix.
- **WP-065 Post-pilot report** — Outcomes vs §2 success criteria, testimonials, unit economics, case study PDF (marketing + future MoH artifact). *AC:* published to repo/docs site.

### 13.7 Catalog — Phase 4 (Months 7–9) · NHIS & growth
- **WP-070 NHIS research pack** — Obtain current NHIS Medicines List + G-DRG tariff schedule + private-facility claims submission process/format (desk research + interviews with 2 accredited-clinic claims officers); ADR-004 fixes export format & claim data model. *AC:* sources archived; sample real (redacted) claim reproduced by hand from our data model on paper.
- **WP-071 Claims module** *(dep 070)* — `ghanaemr-claims` OMOD + `esm-ghana-claims`: NHIS eligibility capture at check-in, visit→claim assembly (ICD-10→G-DRG mapping table as maintained CSV, NHIS-list medicines pricing), validation queue UI, batch export per ADR-004, manual status tracking (submitted/paid/rejected+reason). *AC:* end-to-end claim for 5 visit archetypes matches hand-computed expected values; rejection-reason report exists.
- **WP-072 Tariff sync tooling** *(dep 071)* — Versioned tariff/medicines-list update procedure with diff report and effective-dating. *AC:* simulated tariff update flows through without touching historical claims.
- **WP-073 SMS reminders** *(dep 043)* — Provider adapter (Hubtel or Africa's Talking — pick by pricing/API test, ADR-005) behind §8 interface; appointment + immunization-due reminders; opt-in consent flag + STOP handling; per-clinic sender config. *AC:* sandbox + live test messages; opt-out honored (test); cost-per-message logged.
- **WP-074 Analytics** *(dep 044)* — MariaDB read replica; Metabase over curated SQL views (visits, revenue, morbidity, stock, claims); per-clinic collections with permissions mirroring Data Filter scoping. *AC:* owner dashboard for each pilot clinic; replica lag alarmed.
- **WP-075 Merge & dedup** — Patient-merge tooling/SOP (use platform merge capabilities; verify behavior with Data Filter mappings + audit) + monthly duplicate-scan report. *AC:* merge of a cross-clinic duplicate preserves both clinics' access + full audit trail (test).
- **WP-076 Onboarding automation** *(dep 062,051)* — "New clinic in ≤2 days": scripts for location seed, roles/users, price-list import, printer profile; sales-to-live checklist. *AC:* dry-run onboarding of a fictional clinic in ≤2 days logged.

### 13.8 Catalog — Phase 5 (Months 10–14) · Scale & interop
- **WP-080 50-clinic load proof** *(dep 034)* — k6 profile per §4.3; find breakpoints; tune (pool sizes, heap, MariaDB); ADR-006 documents verified headroom + the sharding trigger metrics. *AC:* targets met at 50-clinic simulation on prod-sized replica env.
- **WP-081 FHIR mirror (SHR-lite)** *(dep 020)* — HAPI FHIR JPA (Postgres 16) service; sync from fhir2 (incremental, idempotent, monitored); resources per §8; IPS summary endpoint; OAuth2 client-credentials; isolation invariants re-proven at the mirror. *AC:* external test client retrieves IPS for a consented patient only; sync lag alarmed; conformance statement published.
- **WP-082 OpenHIM evaluation** — ADR-007: adopt only when a concrete external counterparty (GHIMS/lab/insurer) exists; document integration blueprint either way. *AC:* ADR merged.
- **WP-083 Keycloak SSO + MFA** — Migrate auth to Keycloak (TOTP MFA for admin/clinician roles), fallback plan, session policies. *AC:* MFA enforced for admins; login e2e updated; break-glass local account documented.
- **WP-084 In-country hosting eval** — Ghana DC/cloud options (incl. government/NITA and commercial DCs — current market scan), cost/latency/compliance matrix, migration plan draft. *AC:* ADR-008 with go/no-go criteria tied to public-sector pipeline.
- **WP-085 External security assessment** — Commission pen-test (budget §15); remediate highs/criticals; publish summary attestation. *AC:* zero open criticals; report archived.
- **WP-086 Orchestration revisit** — ADR-009: stay compose (hardened, systemd-managed, documented capacity) vs migrate k3s, per D5 triggers. *AC:* decision + if-migrate plan.
- **WP-087 MoH/GHS engagement pack** — Capability statement (FHIR endpoints, IPS, DHIMS2-compatible aggregate export prototype, Act 843 posture), pilot evidence from WP-065, standards mapping vs published GHS/MoH digital-health requirements (verify current documents). *AC:* pack review-ready; DHIMS2 export prototype demonstrated.
- **WP-088 Community release** — mkdocs site, contribution guide, public demo instance (synthetic data, auto-reset), OpenMRS Talk announcement post. *AC:* an outsider can stand up the distro from docs alone (tested).

## 14. Pilot operations playbook

### 14.1 Per-clinic hardware kit (BOM, ~US$900–1,600 depending on rooms)
| Item | Qty | Notes |
|---|---|---|
| Mini-PC or laptop per workstation (8 GB RAM, Chrome) | 3–6 | Front desk, consult room(s), pharmacy, lab, cashier |
| Dual-SIM 4G failover router (e.g., load-balancing LTE router) + 2 data SIMs (different carriers, e.g., MTN + Telecel/AT) | 1 | Primary broadband where available; router fails over automatically |
| UPS (≥1 kVA) for router + front-desk PC | 1–2 | Power cuts are routine |
| 80 mm thermal receipt printer | 1–2 | Cashier, pharmacy |
| A4 laser printer | 1 | Prescriptions, reports, referrals |
| Barcode scanner (optional) | 1 | MRN cards, Phase 4 |
| Surge protectors, cabling, network switch | — | |

### 14.2 Go-live sequence (from WP-060, reusable)
Site survey & contract → hardware install + connectivity test (latency logged) → clinic metadata + price list + users → historical-patient import (WP-051, optional) → 2-day training (WP-048) → half-day parallel dry run → go-live Monday with you on-site → hypercare (daily calls ×2 weeks) → handover to standard support (WP-061). **Paper fallback SOP:** pre-printed OPD slips mirroring the consult form; re-entry within 24 h by records staff; downtime >30 min triggers status-page + WhatsApp broadcast.

## 15. Budget & cost model

**Monthly infra (pilot, Phases 2–4):** app-1 ≈ $120–180 · db-1 ≈ $60–100 (or $0 if co-located initially) · staging ≈ $30–50 · S3 backups ≈ $5–15 · egress ≈ $10–30 · domain/misc ≈ $5 · Cloudflare/Grafana/Sentry/Uptime-Kuma free tiers ≈ $0 → **≈ $230–380/month** (< $500 ceiling; re-quote exact instance pricing for the chosen region in WP-004). Phase 4 adds replica+Metabase host ≈ +$60–90; SMS is pass-through (~pennies/message via Ghanaian aggregators).
**One-time:** clinic kits $900–1,600 ×3 (decide: included vs clinic-purchased — recommend clinic-purchased at cost to test commitment) · legal review $500–1,500 · pen-test (Phase 5) $3–8k · incorporation/DPC fees (verify current) · travel to clinics.
**Suggested pricing hypothesis (validate in pilot):** setup fee ≈ GHS equivalent of hardware+onboarding at cost, then tiered subscription ~GHS 400–1,200/clinic/month by size, claims module as +add-on. Target: infra+support cost per clinic < 25% of subscription at 20 clinics.

## 16. Business, licensing & sustainability
Open-core-without-the-catch: 100% of code MPL-2.0+HD public; revenue = hosting, onboarding, training, support, claims add-on. Data-portability guarantee in every contract (full FHIR/CSV export on demand) — the anti-LHIMS positioning, stated explicitly in sales material. Community strategy: contribute fixes upstream, publish the distro, present at OpenMRS community calls (recruiting future help = bus-factor mitigation). Government pathway (opportunistic, Phase 5): WP-087 pack + pilot evidence + FHIR/GHIMS-readiness; no dependency on it for survival. Incorporate a Ghanaian entity before charging clinics (legal step alongside WP-052).

## 17. Risks & mitigations (top 10)
| Risk | L×I | Mitigation |
|---|---|---|
| Clinic connectivity outages (cloud-only D7) | H×H | Dual-SIM failover + UPS kit; paper SOP; telemetry on outage-minutes; D7 revisit trigger |
| Solo bus factor | M×H | Everything scripted/runbooked; managed/free-tier services; public code; recruit first support hire from pilot revenue |
| LLM-generated code defects | M×H | Appendix A guardrails; mandatory tests per WP; human review on security/consent/migrations; permanent isolation test suite |
| OpenMRS module version incompatibilities | M×M | Pin by digest; WP-001 matrix; staging soak before prod; upgrade quarterly, never live-first |
| Staff adoption failure / slower-than-paper | M×H | Time-per-visit measured in UAT; queues+forms optimized before go-live; champions per clinic; WP-064 buffer |
| Privacy incident in shared-tenancy model | L×H | §7 invariants as CI-blocking tests; audit; DPA clarity; break-glass reporting; pen-test |
| NHIS claims format assumptions wrong | M×M | WP-070 field research *before* building; export (not API) first |
| Regulatory delay (DPC registration) | M×M | Filed Week 1–2; pilot proceeds with consent + DPA in place; legal counsel line |
| Cloud-region latency hurts UX | L×M | WP-004 measurement first; Cloudflare static caching; perf budgets in CI |
| Competing local vendors / GHIMS expands into private sector | M×M | Speed + open-source trust + network-record USP; interop-ready posture makes us complementary |

## 18. KPIs (auto-collected where possible, WP-063)
Adoption: % OPD visits fully digital (target ≥90%), active users/clinic/day. Efficiency: median registration time, consult-documentation time vs paper baseline (UAT-measured), pharmacy queue time. Reliability: uptime during clinic hours (≥99.5%), outage-minutes/clinic, restore-drill pass rate. Quality: % visits with coded diagnosis (≥95%), duplicate-patient rate (<1%/quarter), claim rejection rate (Phase 4, target <10% then <5%). Trust: consent grants count, break-the-glass events (expect ≈0; each reviewed), audit-review completion. Business: MRR, cost/clinic, onboarding person-days (≤2 by Phase 4 end), NPS from clinic owners.

## Appendix A — LLM execution protocol

**Per-WP prompt template (paste into the coding agent):**
```
You are implementing one work package of Sankofa EHR, an OpenMRS 3-based EHR
for Ghanaian primary-care clinics.

READ FIRST (in order):
1. /docs/ghana-ehr-master-plan.md — Sections 3 (Locked Decisions), 11
   (Engineering standards), and the sections listed in this WP's "Context to load".
2. /work-packages/WP-XXX.md — the full work package (goal, tasks, ACs).
3. /docs/VERSIONS.md and any ADRs referenced.

RULES (non-negotiable):
- Never invent OpenMRS APIs, module capabilities, config keys, CSV headers, or
  version numbers. When unsure, consult the official docs/READMEs (rest.openmrs.org,
  the module's GitHub README, O3 docs) or read the dependency's source in
  node_modules/.m2 — and cite what you found in the PR description.
- Do not contradict Section 3 Locked Decisions. If a task seems to require it,
  STOP and produce a draft ADR instead of code.
- Metadata changes go through Initializer configs, never manual DB edits.
- Every AC maps to an automated test or a logged drill before you claim done.
- Small conventional commits; one PR per WP (or per task for L-sized WPs);
  update all "Artifacts" docs; add/adjust CHANGELOG.
- Security-sensitive surfaces (auth, Data Filter, consent, migrations, backup
  scripts, anything touching ghanaemr_access_audit) → flag "HUMAN REVIEW
  REQUIRED" prominently in the PR.
- If blocked by missing external info (e.g., a Ghana-specific spec), implement
  behind a flag with a mock per Section 8 and file a follow-up note in the WP.

DEFINITION OF DONE: all ACs demonstrably met, CI green (lint, unit, contract,
isolation suite, build), docs updated, WP file status set to "review".
```

**Human review checklist (you, per PR):** touches auth/consent/isolation/migrations/backups? → line-by-line. New dependency? → license + maintenance check. DB changeset? → tested up on a prod-shaped snapshot. Claims/money math? → hand-verify one example. Otherwise: skim + trust CI.

**Sequencing rule:** work strictly in dependency order within a phase; phases may overlap only where WPs have no dependency path between them. Never parallelize two WPs that edit `distro/configs` metadata domains that overlap.

**Verification-first pattern:** any fact in this plan tagged "verify" (versions, Ghana Card checksum, NHIS number format, EPI schedule, district counts, DPC procedures, G-DRG formats, current module names/maintenance status) is a *research sub-task inside the owning WP* — the agent confirms against primary sources, archives the source in `/docs/sources/`, and only then implements. This plan deliberately fixes decisions, not facts it cannot guarantee.

## Appendix B — Reference materials to obtain/consult (archive copies in /docs/sources)
OpenMRS: platform release notes & docs, O3 docs + design system, REST API docs (rest.openmrs.org), fhir2 module docs, Initializer README (authoritative CSV/JSON formats), Data Filter README, idgen/addresshierarchy/attachments/reporting/queue/appointments/stockmanagement/billing module READMEs, reference-application distro repo, OpenMRS Talk (search before building anything novel — someone in Kenya/Uganda has likely done it).
Ghana: Data Protection Act 2012 (Act 843) + DPC registration guidance; Ghana EML & Standard Treatment Guidelines (current editions); NHIS Medicines List + G-DRG tariffs + provider claims-submission guidance; GHS OPD/ANC register formats; Ghana EPI schedule; Ghana Statistical Service admin-boundary lists; NIA Ghana Card documentation; MoH/GHS digital-health & GHIMS announcements (monitor for exchange specs).
Comparable art: KenyaEMR and UgandaEMR+ public repos/configs (primary-care O3 patterns to borrow), Ozone HIS docs (assembly/packaging ideas), OpenHIE architecture spec (Phase 5+), WHO SMART Guidelines (ANC content, Phase 4+ enrichment).

## Appendix C — Glossary
**O3/ESM** OpenMRS 3 frontend / its microfrontend packages · **OMOD** OpenMRS backend module artifact · **Initializer** module that loads metadata from CSV/JSON at startup · **CIEL** Columbia International eHealth Laboratory concept dictionary · **Data Filter** module enforcing row-level, location-based access · **IPS** International Patient Summary (FHIR) · **SHR** Shared Health Record · **GHIMS** Ghana Health Information Management System (state platform + exchange, 2025–) · **LHIMS** its failed proprietary predecessor · **NHIS/NHIA** Ghana's national insurance scheme/authority · **G-DRG** Ghana Diagnosis-Related Groups (NHIS claim tariff grouping) · **DHIMS2** Ghana's DHIS2 aggregate reporting system · **CHAG** Christian Health Association of Ghana · **CHPS** Community-based Health Planning and Services (lowest care tier) · **DPC** Data Protection Commission (Ghana) · **MMDA** Metropolitan/Municipal/District Assembly · **MoMo** mobile money · **ADR** Architecture Decision Record · **RPO/RTO** recovery point/time objective.

---
*End of Master Plan v1.0. First actions: WP-000 → WP-001 (in Claude Code), and WP-005 legal filing in parallel — it has the longest external lead time.*
