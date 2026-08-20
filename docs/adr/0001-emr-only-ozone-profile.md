# ADR-0001: EMR-only Ozone profile with built-in OpenMRS auth

- **Status:** Accepted
- **Date:** 2026-08-19
- **Deciders:** Founding team

## Context

The master plan locks the architecture to the Ozone FOSS distro tooling so that later hospital-tier components (Odoo ERP, SENAITE LIS, Superset analytics, Keycloak SSO) can be *enabled*, not re-integrated. The clinic MVP, however, needs only the OpenMRS 3 EMR. Running the extra apps would add ~3 services' worth of RAM/attack surface per clinic instance and an SSO dependency with zero current benefit (there is only one app to sign into).

Ozone's distro tooling supports this natively: the project is scaffolded from `com.ozonehis:maven-archetype`, inherits build logic from `com.ozonehis:maven-parent` (pinned `1.0.0-alpha.10`), and overlays the base `com.ozonehis:ozone` artifact. Which services run is controlled by a single project file, `distro/scripts/docker-compose-files.txt`, copied into the built distro's `run/docker/scripts/` at package time.

## Decision

1. **EMR-only profile:** `distro/scripts/docker-compose-files.txt` lists only the common infrastructure and OpenMRS compose files. Odoo, SENAITE, Superset, ERPNext, and all EIP integration services are omitted.
2. **Built-in OpenMRS authentication** (no Keycloak) until a second user-facing app ships — SSO with one app is complexity without benefit.
3. **Demo data OFF** — the database starts empty; all metadata comes from `configuration/` via Initializer. (Ozone's demo data is opt-in via `DEMO=true`; we never set it outside throwaway local experiments.)
4. **Every component version pinned:** Ozone parent/base at `1.0.0-alpha.10` in `distro/pom.xml`; O3 frontend/backend images at `3.0.0-beta.18` via the base distro's `.env` (`O3_DOCKER_IMAGE_TAG`). No floating `latest`.
5. **`configuration/` is a symlink** to `distro/configs/openmrs/initializer_config` — the archetype build hardcodes that path, so rather than fork the build, the top-level `configuration/` path from the master plan points into it. Initializer domain folders (e.g. `configuration/patientidentifiertypes/`) are created there and flow into the built distro automatically.

## Consequences

- Per-clinic footprint fits the 4 vCPU / 8 GB sizing in the plan; fewer services to harden, back up, and monitor.
- Hospital tier upgrade path = add lines back to `docker-compose-files.txt` and enable SSO (one-time scripted user migration to Keycloak, per plan §8).
- We track Ozone releases deliberately: bumping the parent/base version is an explicit, tested change.
- Anything requiring a second app (billing via Odoo, labs via SENAITE) triggers a revisit of this ADR.
