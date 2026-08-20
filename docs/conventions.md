# Engineering conventions

These operationalize the principles in the [master plan](ghana-clinic-emr-implementation-plan.md) §0. They apply to every stage.

## Branching & commits

- **Trunk-based development.** `main` is always releasable; nothing merges that breaks the end-to-end demo.
- **Short-lived branches** (< 2 days), merged via PR once CI exists; until then, small atomic commits to `main`.
- **Conventional commits:** `feat:`, `fix:`, `chore:`, `docs:`, `ci:`, `refactor:`, `test:`. Scope optional, e.g. `feat(registration): …`.
- Releases are tagged `v0.<stage>` at each passed test gate.

## Configuration is code

- **All OpenMRS metadata comes from `configuration/` (Initializer domains).** No hand-edits in the admin UI on staging or production — the database is disposable until the pilot.
- Changing a form or metadata file = new version of the file; the old path is deleted in the same release. No compat shims, no dual-writes, no long-lived feature flags.
- Pre-pilot, config/schema changes are destructive: reset the staging DB rather than write migrations. Post-pilot: one-way Liquibase changesets in our own modules only.

## Decisions

- Any custom module (`esm-gh-*`, `omod-gh-*`) or "OpenMRS can't do X" claim requires an **ADR** in `docs/adr/` (template: [adr/template.md](adr/template.md)), citing the docs/Talk threads checked.
- Evaluation spikes are timeboxed (max 2 days) and end in a decision recorded as an ADR — never half-built code.

## Testing & Definition of Done

A task is done when: merged + CI green (including the cumulative e2e suite — earlier stages' tests are never deleted, only updated when behavior intentionally changes) + all metadata expressed in `configuration/` + runbook/ADR updated + demoed on staging.

## Gate ritual

End-of-stage demo to the whole team + a clinic advisor; checklist signed and archived in `docs/gates/`; release tagged; only then does the next stage start. Gates are executed by a non-author. A failed gate stops forward work until fixed.
