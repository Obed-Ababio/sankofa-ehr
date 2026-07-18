# Claude Code instructions — Sankofa EHR

This repo is worked on from **two machines with independent Claude sessions**.

**Session start:** `git pull`, then read `STATUS.md` — it is the cross-machine
handoff (current WP states, founder checklist, machine-specific setup, next
actions). **Session end:** update `STATUS.md` (snapshot + one session-log
line) and push. Machine-local facts (running containers, keys, auth) belong
in its "Machine state" section, not in memory files.

## Ground rules (from `docs/ghana-ehr-master-plan.md` — read §3 + Appendix A)

- Master plan **§3 Locked Decisions (D1–D12) are binding**. Deviations require
  an ADR in `docs/adr/` with founder sign-off BEFORE code. ADR-010 already
  amended D1: platform version tracks the pinned stable RefApp release.
- **Never invent** OpenMRS APIs, module capabilities, config keys, CSV
  headers, or version numbers. Verify against official docs/READMEs/source,
  cite in the PR/commit, archive primary sources in `docs/sources/`.
- `docs/VERSIONS.md` is the only source of version pins — verify, then pin.
- Metadata changes go through Initializer configs (`distro/configs/`), never
  manual DB edits.
- Every WP acceptance criterion maps to an automated test or a logged drill.
- Work packages: `work-packages/` (one file per WP + index README). Update
  the WP file status + progress log and the index when states change.
  Sequencing: dependency order within a phase (Appendix A).
- Conventional Commits; security-sensitive surfaces (auth, Data Filter,
  consent, migrations, backups, `ghanaemr_access_audit`) → flag
  **HUMAN REVIEW REQUIRED**.
- No plaintext secrets anywhere, ever — secrets live SOPS/age-encrypted in
  the private `sankofa-secrets` repo (see `infra/runbooks/secrets.md`).
  CI runs a full-history gitleaks scan on every push.

## Build quirks

- Java builds run via Docker (machines may lack JDK 17):
  `docker run --rm -v <repo>/backend/ghanaemr-core:/src -v ghanaemr-m2:/root/.m2 -w /src maven:3.9-eclipse-temurin-17 mvn -B clean verify`
- `make dev` runs the STOCK RefApp (pinned tag) until WP-010 lands our
  distro; first boot can take ~75 min, restarts ~1 min. Backend readiness =
  `/openmrs/health/started` (container "healthy" lies during first boot).
