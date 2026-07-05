# Developer quickstart

Target: productive on a clean machine in ≤ 30 minutes (WP-002 AC).

## Prerequisites

- Docker Engine + Compose v2 (versions in [`VERSIONS.md`](VERSIONS.md) §5)
- git, GNU make, curl
- For backend module work (from WP-020): JDK 17, Maven
- For ESM work (from WP-022): Node.js LTS 24.x

## Run the stack

```bash
make dev        # starts the stack; first boot loads demo data — 15 min to
                # ~75 min depending on hardware (observed 73 min on WSL2)
make dev-logs   # watch the backend come up ("Started" health endpoint)
```

Then open <http://localhost/openmrs/spa/login> — demo credentials `admin` / `Admin123`
(stock reference-application defaults; our distro replaces these at WP-011).

Until WP-010 lands, `make dev` runs the **stock** O3 reference application at
the tag pinned in `VERSIONS.md` — useful for exploring upstream behavior and
validating the local Docker setup. After WP-010 it switches to our assembly
(`distro/compose.dev.yml`).

## Ground rules (from the master plan)

- Read §3 Locked Decisions before proposing changes; deviations need an ADR.
- Never invent OpenMRS APIs, config keys, CSV headers, or versions — verify
  against official docs and cite in the PR.
- Metadata changes go through Initializer configs in `distro/configs/`,
  never manual DB edits.
- Every WP acceptance criterion maps to a test or a logged drill.
