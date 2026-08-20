# Sankofa EHR

Multi-clinic EMR for Ghanaian private clinics, built on OpenMRS 3 via the Ozone FOSS distro tooling. Clinics first, hospital tier later.

Master plan: [docs/ghana-clinic-emr-implementation-plan.md](docs/ghana-clinic-emr-implementation-plan.md)

## Quick start

```sh
make dev
```

See [docs/dev-setup.md](docs/dev-setup.md) for prerequisites and troubleshooting.

## Repository layout

| Path | Contents |
|---|---|
| `distro/` | Ozone-based distro: docker compose config, pinned app versions |
| `configuration/` | OpenMRS Initializer domains — all metadata as code |
| `frontend/` | Custom O3 microfrontends (`esm-gh-*`), only when an ADR justifies one |
| `backend/` | Custom OpenMRS modules (`omod-gh-*`), same rule |
| `tools/` | Import CLI, seed-data generator, report tally scripts |
| `infra/` | Ansible playbooks, inventory, SOPS-encrypted secrets |
| `docs/` | Runbooks, ADRs (`docs/adr/`), gate checklists (`docs/gates/`) |
| `.github/workflows/` | CI |

## Conventions

Trunk-based development, conventional commits, metadata only via `configuration/`. See [docs/conventions.md](docs/conventions.md).
