## Work package

WP-XXX — <title> (link the file in `work-packages/`)

## What & why

<!-- One paragraph. Reference master plan sections, not vibes. -->

## Sources consulted

<!-- Per Appendix A: never invent OpenMRS APIs, config keys, CSV headers, or
version numbers. Cite the docs/READMEs/source you verified against. -->

## Checklist

- [ ] No Locked Decision (master plan §3) contradicted — or a draft ADR is included instead of code
- [ ] Metadata changes go through Initializer configs (no manual DB edits)
- [ ] Every touched acceptance criterion maps to an automated test or logged drill
- [ ] Docs listed in the WP's "Artifacts" updated; CHANGELOG updated
- [ ] Conventional commits; WP file status updated

## Security-sensitive?

- [ ] **HUMAN REVIEW REQUIRED** — this PR touches auth, Data Filter, consent,
      migrations, backup scripts, or `ghanaemr_access_audit`
