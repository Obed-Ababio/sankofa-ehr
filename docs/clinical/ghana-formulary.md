# Sankofa drug formulary — NHIS Medicines List 2025 — for clinician review

**Status: loaded into the dev EMR as the working formulary; flagged items and
final scope need clinician review (master plan task 2.5).** Unlike the
diagnosis value set, this list is a mechanical derivation of a national
document — the NHIA's own specialists already curated it — so it ships to dev
first and gets reviewed rather than blocking on sign-off. Nothing is live for
patients until the pilot.

## Source and scope

- Source: **NHIS Medicines List, March 2025** (NHIA, 551 formulations, in
  effect from 1 March 2025) — the set of medicines NHIS reimburses, in
  generic/INN names with dosage form and strength. Full parsed copy:
  [`tools/drugs/nhis-ml-2025.csv`](../../tools/drugs/nhis-ml-2025.csv)
  (includes NHIA prices and prescribing levels).
- Scope: prescribing levels **A, M, B1, B2, C** — everything up to a district
  hospital / doctor-led facility. Levels **D** (secondary/tertiary) and **SM**
  (specialist-only) are excluded: 441 formulations in scope.
- Of those, **425 are loaded**; 16 could not be mapped to the international
  dictionary (below).
- Every drug is linked to its CIEL drug concept and dosage-form concept, so
  prescriptions emerge as coded FHIR `MedicationRequest`s — the basis for NHIS
  claims (Stage 5). The O3 demo drug list (320 US-market formulations) is
  retired.

## For the reviewer

1. **The 16 unmapped formulations** — CIEL has no concept for these compound
   galenicals/fluids. Options per item: request a CIEL concept (we can file
   it), create a local concept, or drop from the electronic formulary:
   Aqueous Cream BP · Cholera Replacement Fluid (×2) · Ciprofloxacin +
   Tinidazole · Conjugated Oestrogen + Norgesterol · Corticosteroid +
   Antibiotic ear/eye preparations (×2) · Darrow's Solution · Iron (III)
   Polymaltose (×2) · Simple Linctus BPC (×2) · Soothing Agent + Local
   Anaesthetic ± Steroid rectal preparations (×4).
2. **Concept substitutions worth a glance** (full list in
   [`tools/drugs/pins.csv`](../../tools/drugs/pins.csv), `note` column) — e.g.
   Adrenaline→Epinephrine, Phenoxymethyl Penicillin→Penicillin V, Isophane
   Insulin→Insulin isophane *human*, Promethazine Theoclate→Promethazine
   (theoclate salt absent from CIEL), suspected NHIA typos (Ceftriazone,
   Ephedrine HCI).
3. **Scope check** — is level ≤ C the right cut for the clinics we're
   targeting? Any level-D/SM item a private OPD would realistically stock
   (and any in-scope item you'd drop)?
4. Anything the clinic prescribes routinely that is **not on the NHIS list**
   (cash-and-carry items)? Those can be added as non-NHIS local drugs.

The working set the EMR loads is
[`ghana-formulary-draft.csv`](ghana-formulary-draft.csv) — one row per
formulation with its CIEL mapping and NHIA price/level. Corrections are made
there (or in `pins.csv`) and re-emitted with
`python3 tools/drugs/build-formulary.py emit`.

## Review record

| | |
|---|---|
| Reviewed by (name, qualification) | |
| Date | |
| Outcome | ☐ Confirmed  ☐ Changes requested (listed below) |
| Changes requested | |
