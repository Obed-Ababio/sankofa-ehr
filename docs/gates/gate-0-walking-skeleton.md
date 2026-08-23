# Test Gate 0 — Walking skeleton (closed in local+CI form, 2026-08-21)

| # | Gate item | Result |
|---|---|---|
| 1 | Fresh VM → one command → HTTPS login page < 15 min | ⏸ **Deferred to pre-launch** with task 0.4 (no staging VM). Local equivalent: `make dev` → login page < 10 min including build ✅ |
| 2 | CI green: reproducible image build; smoke e2e in pipeline | ✅ Every push: build → boot → Playwright smoke; distro zip artifact from `main` (ADR-0002) |
| 3 | Staging destroyed & rebuilt by playbook alone | ⏸ Deferred with 0.4. Local equivalent: `make destroy && make dev` verified repeatedly ✅ |
| 4 | Any team member can run `make dev` | ✅ Verified on the founding laptop; WSL2 path documented in dev-setup.md (verify on desktop at first use) |

**Deviations recorded:** staging-dependent items move to the pre-launch checklist (founder decision 2026-08-21 — build/verify all workflows locally first). Release not tagged at gate time; `v0.1` tag covers Stages 0+1 jointly.

Signed: founder (via session review, 2026-08-22) · Engineer: Claude-assisted build
