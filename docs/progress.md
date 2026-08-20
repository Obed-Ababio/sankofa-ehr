# Progress tracker

Status of the [master plan](ghana-clinic-emr-implementation-plan.md), updated at the end of every working session. Newest session first. This file is the "pick up where I left off" entry point on any machine: read it, run `make dev`, continue at **Next up**.

## Stage 0 — Walking skeleton

| Task | Status |
|---|---|
| 0.1 Scaffold EMR-only Ozone distro | ✅ Done (2026-08-19) |
| 0.2 `make dev` local dev experience + `docs/dev-setup.md` | ✅ Done (2026-08-19) |
| 0.3 CI (GitHub Actions: build, smoke e2e, publish image) | ⬜ **Next up** — blocked on GitHub repo |
| 0.4 Staging VM (in-country, provisioned by playbook) | ⬜ Blocked on cloud provider choice |
| 0.5 Conventions doc | ✅ Done (2026-08-19) |
| 0.6 Non-code: DPC registration, OpenMRS Talk/Slack intro | ⬜ Founder action |

**Test Gate 0:** not yet attempted — needs 0.3 + 0.4. Local equivalents verified: one-command boot, clean `destroy → dev` rebuild, empty DB, EMR-only container set.

Pending decisions/inputs:
- GitHub org/repo name (unblocks 0.3)
- Cloud provider for staging VM (unblocks 0.4)
- Verify NHIS number format on a current NHIA card (needed before Stage 1 task 1.1)

## Session log

### 2026-08-19 — Stage 0 kickoff (laptop)
- Repo initialized; monorepo layout per plan §1; Ozone distro scaffolded from `com.ozonehis:maven-archetype` (parent `1.0.0-alpha.10`, O3 `3.0.0-beta.18`).
- EMR-only profile via `distro/scripts/docker-compose-files.txt` (common + openmrs only); demo data off; ADR-0001 records it all.
- `configuration/` is a symlink into `distro/configs/openmrs/initializer_config` — always clone this repo on a Unix filesystem (macOS or *inside* WSL2, never a Windows drive path).
- Verified: `make dev` boots healthy stack at http://localhost (admin/Admin123); FHIR patient count 0; full `make destroy && make dev` rebuild works.
- Machine-local setup (repeat per machine, see [dev-setup.md](dev-setup.md)): Docker runtime, Maven, Ozone profile in `~/.m2/settings.xml`.
