# WP-003 — Accounts & secrets base

| Field | Value |
| --- | --- |
| **Phase** | 0 — Weeks 1–2 |
| **Status** | todo |
| **Depends on** | — |
| **Estimate** | TBD — expand when picking up this WP |

**Goal:** Establish the account and secrets baseline: domain, edge/CDN, registry, storage, monitoring, tailnet, and encrypted secrets management.

**Context to load:**
- `docs/ghana-ehr-master-plan.md` §10 (infrastructure, environments & operations)

**Tasks:**
1. Register the domain (and check the `.gh` variant).
2. Set up Cloudflare.
3. Set up GHCR.
4. Create the S3 bucket.
5. Set up Sentry.
6. Set up Grafana Cloud.
7. Create the Tailscale tailnet.
8. Generate SOPS/age keys.
9. Create the `sankofa-secrets` repo.

**Out of scope:** TBD — expand when picking up this WP

**Acceptance criteria:**
- [ ] Secrets round-trip documented.
- [ ] No plaintext secrets anywhere (CI scanner on).

**Test plan:** TBD — expand when picking up this WP

**Artifacts:** TBD — expand when picking up this WP
