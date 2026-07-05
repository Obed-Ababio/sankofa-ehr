# WP-007 — Staging host

| Field | Value |
| --- | --- |
| **Phase** | 0 — Weeks 1–2 |
| **Status** | todo |
| **Depends on** | WP-003 |
| **Estimate** | TBD — expand when picking up this WP |

**Goal:** Provision and harden `staging-1` via cloud-init and deploy the stock distro to it.

**Context to load:**
- `docs/ghana-ehr-master-plan.md` §10

**Tasks:**
1. Cloud-init provision `staging-1`: Docker, Caddy, Tailscale, UFW default-deny (443 public only), unattended-upgrades.
2. Deploy the stock distro.

**Out of scope:** TBD — expand when picking up this WP

**Acceptance criteria:**
- [ ] HTTPS reachable.
- [ ] SSH only via tailnet.
- [ ] Hardening checklist logged.

**Test plan:** TBD — expand when picking up this WP

**Artifacts:** TBD — expand when picking up this WP
