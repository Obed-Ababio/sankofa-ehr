# Test Gate 1 — Ghana patient registry (closed in local+CI form, 2026-08-22)

| # | Gate item | Result |
|---|---|---|
| 1 | Non-author executes 10-step registration UAT on staging | ⏸ **Deferred to pre-launch** with staging (0.4). UAT script to be written from `docs/sop/front-desk-registration.md` when staging stands up |
| 2 | All Stage-1 Playwright specs green in CI | ✅ 6 specs: smoke, 2× registration, role isolation ×2, 5-path search (+1 `fixme` documenting the FHIR gap) |
| 3 | 5,000 seeded patients: search p95 < 2 s | ✅ 5,052 patients; worst p95 = **666 ms** (name); identifier searches ~10 ms (`tools/seed-patients/seed.py`) |
| 4 | Malformed Ghana Card / NHIS / phone rejected with readable errors | ✅ Spec-verified in UI (phone) + REST (Ghana Card, NHIS) |
| 5 | `GET /ws/fhir2/R4/Patient?identifier=<ghana-card>` returns patient with typed identifiers | ✅ Verified — folder number, OpenMRS ID, Ghana Card all correctly typed |
| 6 | Front Desk verifiably blocked from clinical data | ✅ At REST/service layer (403 vs clinician 200, e2e-enforced). ⚠️ **Known gap:** fhir2 module bypasses privileges — documented in `docs/security/roles.md`, `test.fixme` in suite, Stage 3 hardening item |

**Notable build decisions:** Ghana Card regex widened to ICAO prefixes (research-driven, differs from master plan); folder numbers hyphenless `ACC######X` (Idgen reality); stock roles unusable for isolation (all inherit Privilege Level: High) → computed privilege sets. NHIS 8-digit format **still provisional** — verify against a physical card before pilot.

Signed: founder (via session review, 2026-08-22) · Engineer: Claude-assisted build

Release: `v0.1` tagged at this gate (covers Stages 0+1).
