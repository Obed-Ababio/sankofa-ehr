# WP-000 — Bootstrap

| Field | Value |
| --- | --- |
| **Phase** | 0 — Weeks 1–2 |
| **Status** | review |
| **Depends on** | — |
| **Estimate** | TBD — expand when picking up this WP |

**Goal:** Stand up the GitHub org and public monorepo skeleton with license, docs, and templates so an empty repo builds with CI green.

**Context to load:**
- `docs/ghana-ehr-master-plan.md` §11 (engineering standards, repo layout, CI/CD, testing)

**Tasks:**
1. Create the GitHub org.
2. Create the public monorepo skeleton per §11.
3. Add MPL-2.0 + Healthcare Disclaimer (HD) license.
4. Write the README.
5. Add the ADR template.
6. Create the WP tracker files (this directory).
7. Add issue and PR templates.

**Out of scope:** anything beyond skeleton (distro assembly is WP-010; CI beyond the validate job is WP-008).

**Acceptance criteria:**
- [ ] Repo builds; empty CI is green. *(workflow written; needs a GitHub remote to actually run — see log)*

**Test plan:** `.github/workflows/ci.yml` validate job (structure check, Zone.Identifier check, gitleaks secret scan) green on first push.

**Artifacts:** repo skeleton per §11, `LICENSE` (MPL-2.0+HD, fetched from openmrs-core), `README.md`, `CHANGELOG.md`, `.gitignore`, `docs/adr/` (template + pre-assigned index), `work-packages/` (71 WP files + index), `.github/` (PR template, 2 issue templates, CI workflow).

---

## Progress log

**2026-07-05** — Local bootstrap complete: skeleton per §11, license (genuine MPL-2.0 + Healthcare Disclaimer text fetched from openmrs-core, project-specific tail), README, CHANGELOG, ADR template + index (ADR-001…009 pre-assigned per plan), all 71 WP tracker files generated from §13, issue/PR templates, CI validate workflow, master plan moved to `docs/`.

**Human actions remaining (outward-facing, not agent-executable):**
1. Create the GitHub org + public repo (plan names it `sankofa-ehr`; this working dir is `chronicles` — rename or set the remote name at push time).
2. `git push` → confirm CI green (closes the AC).
3. Create the private `sankofa-secrets` repo (WP-003).
4. Update the security-advisories link in `.github/ISSUE_TEMPLATE/config.yml` with the real org/repo slug.
