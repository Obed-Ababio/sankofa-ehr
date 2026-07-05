# WP-004 — Region selection harness

| Field | Value |
| --- | --- |
| **Phase** | 0 — Weeks 1–2 |
| **Status** | in-progress |
| **Depends on** | — |
| **Estimate** | TBD — expand when picking up this WP |

**Goal:** Measure latency/throughput from Ghanaian vantage points to candidate cloud regions and record the pick in ADR-001 per decision D6.

**Context to load:**
- `docs/ghana-ehr-master-plan.md` §3 (decision log, D6)

**Tasks:**
1. Write a script measuring latency/throughput from Ghanaian vantage points (clinic links when available; Ghana-based VPS / RIPE Atlas otherwise) to af-south-1, eu-west-1/2, and Azure ZA-North.
2. Record the region pick in ADR-001 per D6.

**Out of scope:** picking the region from non-Ghana measurements; throughput testing beyond TTFB (add iperf-style checks only if TTFB results are ambiguous).

**Acceptance criteria:**
- [ ] Data table produced **from a Ghanaian vantage point**.
- [ ] Signed ADR-001 committed.

**Test plan:** harness smoke test from any vantage (done, logged below); the AC data table comes from ≥2 Ghana vantage runs (different carriers), archived in `docs/sources/`.

**Artifacts:** `infra/region-latency/measure.sh`, `infra/region-latency/endpoints.txt`, ADR-001 (pending).

---

## Progress log

**2026-07-05** — Harness written and smoke-tested (3 samples/endpoint from a non-Ghana WSL vantage; script + CSV output + median summary all work). Endpoints: AWS af-south-1 / eu-west-1 / eu-west-2 (S3 + DynamoDB public endpoints) and Cloudflare edge. **Azure ZA-North has no stable public per-region endpoint** — a throwaway storage account in that region must be created before the real run (instructions in `endpoints.txt`).

**Human actions remaining:** run `./measure.sh 20 <vantage-label>` from Ghanaian vantage(s) — a clinic link, a Ghana VPS, and/or RIPE Atlas probes; ≥2 different carriers (MTN + Telecel/AT); archive CSVs in `docs/sources/`; then draft and sign ADR-001.
