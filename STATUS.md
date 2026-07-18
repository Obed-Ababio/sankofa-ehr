# STATUS — cross-session handoff

**Purpose:** the founder works from two computers with independent Claude Code sessions. This file is the shared state. **Every session: read this first; before ending, update it and `git push`.** (Convention: `git pull` at session start — another machine may have pushed.)

_Last updated: 2026-07-18 · machine A (WSL2) · session: Phase-0 walkthrough_

---

## Where the project stands

**Phase 0 (Foundations) — mostly done.** Repo public at **github.com/Obed-Ababio/sankofa-ehr**, CI green (validate + backend jobs). Private secrets repo: **github.com/Obed-Ababio/sankofa-secrets** (SOPS/age).

| WP | Status | One-liner |
|---|---|---|
| WP-000 Bootstrap | **done** | Skeleton, license, CI, 71 WP trackers; pushed; CI green run 28820657151 |
| WP-001 Versions & stock spin-up | review | `docs/VERSIONS.md` all-cited; stock RefApp 3.7.0 login verified locally (first boot ~73 min, restarts ~1 min) |
| WP-002 Dev env & Makefile | in-progress | `make dev` etc. work; clean-machine 30-min validation still unlogged — machine B setup below is the natural test |
| WP-003 Accounts & secrets | in-progress | Agent half done (age/sops, secrets repo, runbook — both ACs met). Founder checklist below. |
| WP-004 Region latency harness | in-progress | Harness smoke-tested; real runs need Ghana vantage; ADR-001 pending |
| WP-005 Compliance pack | review | 6 Act 843 drafts in `docs/compliance/` await lawyer review; DPC portal = app.dataprotection.org.gh |
| WP-006 Brand & naming | in-progress | **Decision: keep "Sankofa EHR"; register sankofaehr.com only.** Logo/theming blocked on WP-010 |
| WP-020 ghanaemr-core | review | OMOD scaffold; 46 tests, 100% cov; load-verified on live platform (`/ghanaemr/ping` → 200) |

**Key decisions:** ADR-010 **accepted** (platform tracks the pinned stable RefApp ⇒ core 2.8.7); GitHub = personal account (org later via transfer); name = Sankofa EHR; domain = sankofaehr.com only.

## Founder (human) checklist — live state

- [x] age private key backed up offline (password manager)
- [ ] Cloudflare account + 2FA (walkthrough given; completion unconfirmed)
- [ ] **Register sankofaehr.com** at Cloudflare Registrar (availability verified 2026-07-06; auto-renew ON)
- [ ] AWS account (+ root MFA, IAM user) — walkthrough not yet given
- [ ] Tailscale tailnet (login with GitHub)
- [ ] Grafana Cloud + Sentry free tiers (deferrable to WP-045)
- [ ] Later, before branding/incorporation money: manual `.com.gh` check (nic.gh WHOIS, captcha), ORC company-name search, RGD trademark search (classes 9/42/44)

## Next up (either machine)

1. **WP-010 distro assembly** — unblocked by ADR-010; the gateway to all clinical config work. Biggest engineering item.
2. WP-007 staging host — blocked on AWS/hosting account + Tailscale.
3. Lawyer review of `docs/compliance/` drafts; then DPC filing.
4. Ghana-vantage latency runs → ADR-001 (`infra/region-latency/measure.sh 20 <label>`).

## Machine state (things git does NOT carry)

**Machine A — WSL2 laptop (this one):**
- `gh` authed as Obed-Ababio; `age`/`age-keygen`/`sops` + `gh` in `~/.local/bin`
- **age private key** at `~/.config/sops/age/keys.txt` (0600)
- JDK 11 only → Java builds run via Docker: `maven:3.9-eclipse-temurin-17` with named volume `ghanaemr-m2` (see `backend/ghanaemr-core/README.md`)
- Stock RefApp 3.7.0 stack running in Docker (project `refapp-370`, scratchpad checkout) **with ghanaemr.core loaded** — admin/Admin123 at http://localhost/openmrs/spa/login

**Machine B — setup on first session (≈15 min + first boot):**
1. `git clone https://github.com/Obed-Ababio/sankofa-ehr.git && cd sankofa-ehr`
2. `gh auth login` (interactive terminal, not via Claude)
3. Install `age` + `sops` binaries to `~/.local/bin` (commands in `infra/runbooks/secrets.md` context; no sudo needed)
4. **Restore the age private key** from the password-manager backup → `~/.config/sops/age/keys.txt`, `chmod 600`. Never commit or paste it.
5. `git clone https://github.com/Obed-Ababio/sankofa-secrets.git` (sibling dir); verify: `sops -d sankofa-secrets/staging.env`
6. Optional: `make dev` (needs Docker; first boot up to ~75 min) — and log the timed run in WP-002.

## Session log (newest first; one line per session per machine)

- 2026-07-18 · A · Founder walkthrough: ADR-010 signed; repo pushed, CI fixed (gitleaks full-history) & green; WP-000 done; secrets base built (WP-003 ACs met); naming decided (Sankofa EHR / sankofaehr.com); STATUS.md + CLAUDE.md created.
- 2026-07-05/06 · A · Bootstrap session: WP-000/001/002/004/005/006/020 executed to the states above; stock O3 verified; ADR-010 drafted.
