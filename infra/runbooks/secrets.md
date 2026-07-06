# Runbook — Secrets handling (WP-003)

**Rule: no plaintext secret ever touches this repo, CI logs, or chat.** The
CI gitleaks job scans full history on every push; treat a hit as an incident
(rotate the secret, then scrub history).

## Layout

| What | Where |
|---|---|
| Encrypted secret files | Private repo [`sankofa-secrets`](https://github.com/Obed-Ababio/sankofa-secrets) (SOPS + age) |
| age **private** key | `~/.config/sops/age/keys.txt` on the operator machine only, mode 600 |
| age private key backup | Offline: password manager entry + printed copy in a safe place. Losing it = losing every secret. |
| age **public** key (safe to share) | `age1px2c0d98jthe865rnhgtr2zh73lehdc9u2hdg6lzky3g773fpprqzxs83s` (also in `sankofa-secrets/.sops.yaml`) |
| Tooling | `age`/`age-keygen` v1.3.1, `sops` v3.13.2 in `~/.local/bin` (binary installs, no sudo) |

## Round-trip (verified 2026-07-06)

```bash
cd sankofa-secrets
sops staging.env              # edit in $EDITOR; re-encrypts on save
sops -d staging.env           # decrypt to stdout
sops -e -i newfile.env        # encrypt a new file in place
```

Deploy-time pattern (WP-007/WP-044): decrypt on the operator machine, ship to
the host over the tailnet, or decrypt on-host with a host-scoped age key —
decided when the staging host exists. Never bake decrypted values into images.

## GitHub Actions secrets

CI-needed values (e.g., deploy SSH key in WP-008) go in GitHub repo
**Actions secrets** (Settings → Secrets and variables → Actions), never in
workflow files. The sankofa-secrets repo remains the source of truth; GitHub
copies are cache.
