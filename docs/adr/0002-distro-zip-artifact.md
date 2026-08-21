# ADR-0002: The deployable artifact is the distro zip, not a container image

- **Status:** Accepted
- **Date:** 2026-08-20
- **Deciders:** Founding team

## Context

The master plan (task 0.3) says "push image to GHCR tagged with semver + git SHA". That wording assumed we'd build our own container image. In practice, the Ozone tooling works differently: every container in the stack runs an **upstream pinned image** (OpenMRS backend/frontend, MariaDB, nginx…), and our build (`mvn clean package`) produces a **distro zip** — compose files, configs, module binaries, and scripts that those upstream images mount at runtime. There is no "our image" to build; re-packaging upstream images into a custom one would add a build step, a registry, and an update path for zero benefit (guideline: simplest implementation that fully meets requirements).

## Decision

- The release artifact is `distro/target/sankofa-emr-<version>.zip`, built reproducibly in CI and uploaded from `main` (workflow artifact now; attached to a GitHub Release when we tag `v0.x` at stage gates).
- Deployment (Stage 0 task 0.4 onward) means: unpack the zip on the target host, `start.sh` pulls the pinned upstream images. The Ansible playbook automates exactly what CI does.
- Image provenance is controlled by version pins: Ozone parent in `distro/pom.xml`, O3 image tag in the distro `.env`.

## Consequences

- No GHCR publishing, registry credentials, or image lifecycle to manage.
- CI proves the artifact honestly: the same zip it uploads is the one it booted and smoke-tested.
- If we later ship custom modules baked into images (or need air-gapped clinic boxes with pre-pulled images), revisit — that is the point where building/publishing our own images earns its keep.
