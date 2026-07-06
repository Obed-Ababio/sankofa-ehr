# ADR-010 — Platform version: D1's 2.7.x vs RefApp 3.7.0's core 2.8.7

- **Status:** accepted
- **Date:** drafted 2026-07-05; accepted 2026-07-06
- **Deciders:** Obed Ababio (founder)
- **Relates to:** master plan §3 D1, §5, WP-001, WP-010

## Context

Locked Decision D1 pins "OpenMRS Platform 2.7.x + O3 frontend, assembled from
the official reference-application distro pattern". WP-001 version research
(see `docs/VERSIONS.md`, verified 2026-07-05) found these two halves of D1 now
point at different platform versions:

- Latest 2.7.x patch is core **2.7.9** (tags + Maven artifacts; GitHub
  releases lag at 2.7.6).
- The current reference application distro, **RefApp 3.7.0** — the assembly
  D1 tells us to derive from — builds against core **2.8.7**
  (`openmrs.version` in its `distro/pom.xml`), and its published module set
  (initializer 2.12.0, fhir2 4.1.0, webservices.rest 3.5.0, queue 3.0.0,
  stockmanagement 3.0.0, billing 2.3.0, …) is tested against 2.8.x.

Staying on 2.7.9 means diverging from the exact assembly upstream ships and
tests — the opposite of D2's "stay close to upstream" intent. Following the
RefApp means platform 2.8.x, which technically contradicts D1's "2.7.x".

## Decision

Track the platform version the current stable reference application ships
(today: core 2.8.7 via RefApp 3.7.0), i.e. read D1 as "the platform version
of the pinned stable RefApp release", not a hard 2.7.x pin. WP-010 (distro
assembly) derives from RefApp 3.7.0 unmodified in this respect.

## Consequences

- Maximum upstream alignment: our module matrix is exactly the one upstream
  CI tests; free security patches per D2's rationale.
- `VERSIONS.md` §1 keeps 2.7.9 recorded for reference, but the operative pin
  becomes the RefApp's platform version.
- D1's text in the master plan should be annotated to reference this ADR.

## Alternatives considered

- **Hold platform at 2.7.9, keep RefApp 3.7.0 frontend** — rejected
  (proposed): requires re-testing every module against 2.7.9 ourselves,
  exactly the maintenance burden D2 exists to avoid.
- **Use an older RefApp release that shipped 2.7.x** — rejected (proposed):
  forfeits current module fixes; upstream support and docs move forward.
