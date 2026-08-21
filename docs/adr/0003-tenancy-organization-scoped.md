# ADR-0003: Tenancy is organization-scoped; cross-organization sharing arrives later via HIE

- **Status:** Accepted
- **Date:** 2026-08-21
- **Deciders:** Founder + engineering

## Context

The founder's end goal is that facilities maintain their own patients **and** can eventually access records of patients already known to the wider system. The master plan locked "one containerized instance per clinic" for hard data isolation. Taken literally, that blocks even same-owner clinic groups (2–3 branches, one legal entity) from sharing records — their most basic expectation. Conversely, making cross-*organization* sharing the MVP default would require a patient consent framework, inter-organization data-sharing agreements, DPC engagement, and a central patient index before the first pilot.

Cross-organization record access is an **exchange** problem, not a tenancy problem: the standard architecture (OpenHIE) connects independent instances through a client registry keyed on strong identifiers, over FHIR. Our Stage 1–2 decisions (coded data, FHIR contract tests, Ghana Card as optional-but-unique identifier) already build toward that.

## Decision

1. **One instance per organization** (data controller), not per facility. A single-clinic organization is the degenerate case — identical to the old model.
2. **Branches are Locations** within the instance: *Organization root → Facility/branch → rooms (Registration, Triage, Consultation…)*. Patients are org-wide; visits and folder-number prefixes are per-branch.
3. **Cross-organization sharing is deferred to the HIE layer** (Stage 6 horizon): independent instances + central client registry (Ghana Card / NHIS as matching keys) over FHIR R4, contingent on consent framework and DPC/GHS engagement. No cross-org database merging, ever.

## Consequences

- Clinic groups get multi-branch record sharing with zero extra compliance surface (one controller under Act 843).
- Fleet inventory now models organizations; per-branch config (folder prefix, location names) lives inside one instance's `configuration/`.
- The FHIR contract tests (task 2.9) graduate from "nice guarantee" to the load-bearing foundation of the cross-org roadmap.
- Revisit trigger: first real demand for cross-org lookup (e.g., a referral network) → start the HIE spike + DPC consent work, per plan §8.
