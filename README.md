# Sankofa EHR

**A cloud-hosted, OpenMRS 3–based electronic health record network for Ghanaian private primary-care clinics.**

> *Sankofa* (Adinkra): "go back and get it" — apt for retrieving patient records. Working codename; naming/trademark check is WP-006.

One logical patient record across a network of member clinics: a patient registered at any clinic can, with consent, be seen with full history at any other. Built as a thin distribution on upstream [OpenMRS 3](https://openmrs.org) — configuration and small custom modules, not a fork.

## Status

**Phase 0 — Foundations.** Repo bootstrapped (WP-000); version pinning and stock O3 spin-up in progress (WP-001). See [`work-packages/`](work-packages/) for the live backlog.

## Documents to read first

| Doc | What it is |
|---|---|
| [`docs/ghana-ehr-master-plan.md`](docs/ghana-ehr-master-plan.md) | The master plan: architecture, locked decisions, full work-package catalog. **Section 3 (Locked Decisions) is binding** — deviations require an ADR. |
| [`docs/VERSIONS.md`](docs/VERSIONS.md) | Single source of truth for every pinned version, with source links. Never invent a version — verify, then pin. |
| [`docs/adr/`](docs/adr/) | Architecture Decision Records. |
| [`work-packages/`](work-packages/) | One file per work package (WP) with status; the implementation backlog. |

## Repository layout

```
distro/            The product assembly
  configs/         Initializer metadata-as-code (CSV/JSON) — the heart of the product
  frontend/        O3 assembly: spa config, import map, branding, esm config
  openmrs/         distro.properties / module list, Dockerfiles
backend/           Custom Maven OMODs (ghanaemr-core; ghanaemr-claims in Phase 4)
frontend-esms/     Custom O3 microfrontends (network patient search + consent UI)
infra/             Cloud-init, compose files, Caddyfile, backup scripts, runbooks/
qa/                Playwright e2e, k6 load tests, seed-data generators
docs/              Master plan, ADRs, VERSIONS.md, compliance, sources archive
work-packages/     WP tracker: one md file per WP with status + PR links
```

## Principles (from the master plan)

- **Stay close to upstream** (D2): configuration via [Initializer](https://github.com/mekomsolutions/openmrs-module-initializer), O3 config schemas, and JSON forms; thin custom modules only. No forks.
- **Metadata-as-code** (§4.2): a fresh environment must be reproducible from `git clone + docker compose up + initializer run`.
- **One shared instance, row-level isolation** (D4): each clinic is a Location subtree; the Data Filter module enforces per-clinic visibility; cross-clinic access is consent-gated and audited (§7).
- **Interoperability-first**: FHIR R4 from day one; GHIMS-exchange-ready facade in Phase 5.
- **Tests are the definition of done**: every WP acceptance criterion maps to an automated test or a logged drill.

## Getting started (developers)

Prerequisites: Docker + Compose v2, git. Exact versions in `docs/VERSIONS.md`.

```bash
# Local dev stack (WP-002 will add make targets; until then see distro/)
docker compose -f distro/compose.dev.yml up   # placeholder — lands with WP-010
```

## Contributing

Conventional Commits; one PR per WP (or per task for large WPs); Java code needs JUnit ≥ 80% line coverage on service code; TS needs ESLint/Prettier + unit tests for logic-bearing components. Security-sensitive surfaces (auth, Data Filter, consent, migrations, backups) require human review — flag **HUMAN REVIEW REQUIRED** in the PR. See §11 of the master plan and [`.github/PULL_REQUEST_TEMPLATE.md`](.github/PULL_REQUEST_TEMPLATE.md).

## License

[MPL 2.0 with Healthcare Disclaimer](LICENSE) — matching OpenMRS. This is a hosted-service business on 100% open code: no vendor lock-in, contractually guaranteed data export (FHIR/CSV).

**Healthcare disclaimer:** this software is not a substitute for professional clinical judgment; see LICENSE.
