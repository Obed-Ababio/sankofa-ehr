# ADR-0004: O3's built-in print is not a prescription — build `esm-gh-prescription-print`

- **Status:** Accepted
- **Date:** 2026-08-24
- **Deciders:** Founding team (spike executed per master plan task 2.6)

## Context

Task 2.6 timeboxes a spike on O3's built-in printing: if it can produce an
acceptable outpatient prescription, use it; if not, commit to our first custom
frontend module. The spike ran against the real stack with the NHIS formulary
loaded (task 2.5): a full prescribing flow — OPD visit, order basket,
Amoxicillin Capsule 250 mg, dose 500 mg oral thrice daily × 5 days, quantity
15, indication URTI — then every print affordance the UI offers.

Findings:

- The only print affordance is the medications app's `showPrintButton`
  (disabled by default; we enabled it via config). It prints **the contents of
  the Active/Past Medications datatable** via react-to-print — its own config
  description says exactly that. The per-medication row menu offers only
  Modify / Discontinue; there is no per-prescription print anywhere.
- What that printout structurally cannot carry, measured against the NHIS
  Medicines List 2025 dispensing/prescribing guidelines (§7–8: prescriber
  signature in ink, patient name and address, diagnosis, exact quantity to
  supply) and ordinary Ghanaian practice: **no facility identity/letterhead,
  no prescriber name or signature block, no diagnosis, no folder number or
  patient address, no date-of-issue block** — and it prints the *entire
  active-medications list*, not the prescription just signed. No A5 or 80 mm
  thermal layouts; plain A4 browser print only.
- Order form quirk noted for 2.4/2.8 config: quantity units default to
  "Milligram" even for capsules — needs `quantityUnits` defaulting attention
  when the consultation workflow is polished.

An A4 table of active medications is a usable *medication list* (worth keeping
enabled for chart review), but it is not a document a pharmacy can dispense
against or the NHIA would accept on a claim audit.

## Decision

- Build **`esm-gh-prescription-print`** as the project's first custom O3
  frontend module, per the plan's fallback: react-to-print, two layouts —
  **A5** (clinic letterhead prescriptions) and **80 mm** (thermal receipt
  printers common in Ghanaian pharmacies).
- Content: facility name/address/contact, prescriber name + GMDC/registration
  line + signature space, date, patient name/age/sex/folder number, coded
  diagnoses for the visit, and the visit's drug orders (generic name, form,
  strength, dose, frequency, duration, quantity) — the NHIS guideline fields.
- **Sequenced after task 2.4**: a printed prescription should carry the
  diagnosis, which needs the signed-off value set loaded. Building the module
  before then would print an incomplete document.
- Keep `showPrintButton: true` meanwhile — the medication-list printout has
  standalone value and costs nothing.

## Consequences

- First custom ESM introduces a frontend build step to the distro (new
  repo/dir, versioned like config; import-map/route registration). We accept
  that cost now that a concrete document requires it — nothing lighter meets
  the requirement.
- The Stage 2 gate's timed run (registration → printed prescription ≤ 6 min)
  is measured against this module, not the built-in table print.
- Revisit if O3 upstream ships a real prescription-print feature before we
  build ours (check at each O3 version bump).
