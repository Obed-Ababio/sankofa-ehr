# WP-005 — Compliance kickoff (human+agent)

| Field | Value |
| --- | --- |
| **Phase** | 0 — Weeks 1–2 |
| **Status** | review |
| **Depends on** | — |
| **Estimate** | M (agent drafting done; human filing/legal review remains) |

**Goal:** Produce a filing-ready Ghana data-protection compliance pack: DPC registration draft, privacy notice, consent texts, clinic DPA template, breach runbook, and retention-policy memo.

**Context to load:**
- `docs/ghana-ehr-master-plan.md` §9 (security, privacy & compliance — Ghana Data Protection Act, 2012 — Act 843)

**Tasks:**
1. [x] Research DPC processor registration and draft the application notes (procedure, portal, fees, renewal verified against dataprotection.org.gh and L.I. 2512).
2. [x] Draft privacy notice v0.
3. [x] Draft consent texts v0 (registration, cross-clinic sharing, SMS opt-in).
4. [x] Draft clinic DPA template v0.
5. [x] Draft breach runbook v0 (Act 843 §31 requirements verified against the Act text).
6. [x] Write the retention-policy memo (Act 843 §24/§25/§33 verified; GHS/MoH retention guidance NOT found online — marked UNVERIFIED with follow-up list).

**Out of scope:** Actual DPC filing and fee payment; lawyer engagement; final legal pack (WP-052); translations of patient-facing texts (D12); clinic-side controller registrations (clinic obligation, tracked in onboarding kit WP-049).

**Acceptance criteria:**
- [ ] Filing-ready pack reviewed by the project owner (and ideally a Ghanaian lawyer — budget line exists).
- [x] Every legal claim in the pack is either cited to a fetched primary source with access date, or explicitly marked "UNVERIFIED — confirm with DPC/GHS/counsel".

**Test plan:** Document review only (no code). Verification = citations in `docs/sources/wp-005-sources.md` resolve; archived source PDFs present in `docs/sources/`.

**Artifacts:**
- `docs/compliance/dpc-registration-notes.md`
- `docs/compliance/privacy-notice-v0.md`
- `docs/compliance/consent-texts-v0.md`
- `docs/compliance/clinic-dpa-template-v0.md`
- `docs/compliance/breach-response-runbook-v0.md`
- `docs/compliance/retention-policy-memo-v0.md`
- `docs/sources/wp-005-sources.md` (+ archived `act-843-data-protection-act-2012.pdf`, `dpc-fees-li-2512-2025.pdf`)

## Progress log

**2026-07-05 — v0 pack drafted (agent).** All six compliance artifacts drafted and
banner-marked "DRAFT v0 — requires review by a Ghana-qualified lawyer and DPC
verification". Key verified findings:

- Registration portal is **app.dataprotection.org.gh** (not register.dataprotection.org.gh); both controllers and processors must register; renewal every 2 years (Act 843 §50), renewal window 3 months before expiry to 7 days after.
- Current fees per L.I. 2512 (2025): Small GHS 156 / Medium GHS 1,170 / Large GHS 2,340 / Specialised GHS 5,000.
- Act 843 §31 breach notification: notify Commission and data subject "as soon as reasonably practicable" — no fixed statutory deadline; runbook adopts an internal 72-hour standard and 24-hour processor→controller notice in the DPA.
- Health data is "special personal data" (§96 definition); §30(2)–(3) mandate a written controller–processor contract with confidentiality/security terms — implemented by the DPA template.

**UNVERIFIED items carried forward** (flagged inline in the docs): DPC fee-class
treatment of a health-data processor; registration processing SLA; whether health-data
processing is designated "assessable" (§57); any DPC breach-deadline guidance beyond
the statute; GHS/MoH/HeFRA retention periods for private clinics; limitation-period
retention floor.

**Remaining human actions (blocking AC #1):**
1. Founder review of all six drafts.
2. Engage Ghana-qualified lawyer (budget line exists) — review pack, especially DPA clauses 7 (offshore hosting), 8 (notification), 10 (exit/export), and the consent texts.
3. Incorporate the Ghanaian operating entity (prerequisite; master plan §16).
4. Create the DPC portal account, confirm fee class with the DPC, complete and submit the processor registration, pay the fee.
5. Phone/email DPC and GHS/HeFRA to close the UNVERIFIED items; update the memos.
6. Feed final texts into WP-052 (legal pack final) and clinic onboarding kit (WP-049).
