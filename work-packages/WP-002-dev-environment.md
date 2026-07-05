# WP-002 — Dev environment & Makefile

| Field | Value |
| --- | --- |
| **Phase** | 0 — Weeks 1–2 |
| **Status** | in-progress |
| **Depends on** | WP-001 |
| **Estimate** | TBD — expand when picking up this WP |

**Goal:** Provide `make dev/test/build/seed` targets and a contributor quickstart doc.

**Context to load:**
- `docs/ghana-ehr-master-plan.md` §11

**Tasks:**
1. Implement `make dev`, `make test`, `make build`, `make seed`.
2. Write the contributor quickstart doc.

**Out of scope:** the real distro stack (`make dev` wraps the stock refapp until WP-010); test/build/seed implementations (owned by WP-020/WP-010/WP-031 — targets fail with pointers until then).

**Acceptance criteria:**
- [ ] Clean-machine 30-minute setup validated.

**Test plan:** timed clean-machine (or fresh WSL distro / VM) walkthrough of `docs/dev-quickstart.md`, logged here.

**Artifacts:** `Makefile`, `docs/dev-quickstart.md`.

---

## Progress log

**2026-07-05** — `Makefile` (dev/dev-logs/dev-down/dev-destroy real; test/build/seed are honest placeholders pointing at their owning WPs) + `docs/dev-quickstart.md`. `make dev` clones the stock refapp at the `VERSIONS.md`-pinned tag into git-ignored `.cache/`. Remaining: the timed clean-machine validation run, and re-pointing `dev` at `distro/compose.dev.yml` when WP-010 lands.
