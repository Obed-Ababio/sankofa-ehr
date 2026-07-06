# WP-003 — Accounts & secrets base

| Field | Value |
| --- | --- |
| **Phase** | 0 — Weeks 1–2 |
| **Status** | in-progress |
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

**Out of scope:** provisioning any VM (WP-007), DNS records beyond registration (WP-007), backup jobs into the S3 bucket (WP-009).

**Acceptance criteria:**
- [x] Secrets round-trip documented. *(`infra/runbooks/secrets.md`; encrypt/decrypt verified 2026-07-06.)*
- [x] No plaintext secrets anywhere (CI scanner on). *(Full-history gitleaks scan green on every push since 2026-07-06.)*

**Test plan:** sops encrypt→decrypt round-trip logged in the runbook; gitleaks CI job as the permanent guard.

**Artifacts:** `infra/runbooks/secrets.md`, private repo `sankofa-secrets` (`.sops.yaml`, placeholder `staging.env`, README).

---

## Progress log

**2026-07-06** — Agent-side half done:
- age v1.3.1 + sops v3.13.2 installed (binary, no sudo). Keypair generated; private key at `~/.config/sops/age/keys.txt` (0600, never in git). Public recipient recorded in the runbook.
- Private repo **github.com/Obed-Ababio/sankofa-secrets** created and pushed: `.sops.yaml` (age recipient rule), placeholder `staging.env` (encrypted, round-trip verified), README with edit/rotate/add-recipient procedures.
- `infra/runbooks/secrets.md` added to the public repo.

**Human actions remaining (browser/payment — checklist with the founder):**
1. **Back up the age private key offline** (password manager + printed copy) — highest priority; everything else is recoverable, this is not.
2. Domain registration (+ `.com.gh` check at a NIC Ghana registrar).
3. Cloudflare account; add domain, set nameservers.
4. AWS account (S3 backups bucket + candidate af-south-1 region; bucket itself can wait for WP-009).
5. Tailscale tailnet (login with GitHub account).
6. Grafana Cloud free tier; Sentry free tier (can defer to WP-045 if desired).
7. GHCR: no signup needed (part of GitHub) — enabled when WP-008 first pushes an image.
