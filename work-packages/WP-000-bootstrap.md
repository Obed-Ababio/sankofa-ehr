# WP-000 — Bootstrap

| Field | Value |
| --- | --- |
| **Phase** | 0 — Weeks 1–2 |
| **Status** | done |
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
- [x] Repo builds; empty CI is green. *(2026-07-06: run 28820657151 on Obed-Ababio/sankofa-ehr — validate + backend jobs both success.)*

**Test plan:** `.github/workflows/ci.yml` validate job (structure check, Zone.Identifier check, gitleaks secret scan) green on first push.

**Artifacts:** repo skeleton per §11, `LICENSE` (MPL-2.0+HD, fetched from openmrs-core), `README.md`, `CHANGELOG.md`, `.gitignore`, `docs/adr/` (template + pre-assigned index), `work-packages/` (71 WP files + index), `.github/` (PR template, 2 issue templates, CI workflow).

---

## Progress log

**2026-07-05** — Local bootstrap complete: skeleton per §11, license (genuine MPL-2.0 + Healthcare Disclaimer text fetched from openmrs-core, project-specific tail), README, CHANGELOG, ADR template + index (ADR-001…009 pre-assigned per plan), all 71 WP tracker files generated from §13, issue/PR templates, CI validate workflow, master plan moved to `docs/`.

**2026-07-06** — Pushed to **github.com/Obed-Ababio/sankofa-ehr** (public; founder chose personal account over an org for now — transferable later with URL redirects). First push surfaced a gitleaks-action edge case (event range uses root-commit^); replaced with a pinned full-history CLI scan. CI green (validate + backend). Security-advisories link updated to the real slug. Remaining elsewhere: private `sankofa-secrets` repo is WP-003's first task. **WP-000 done.**
