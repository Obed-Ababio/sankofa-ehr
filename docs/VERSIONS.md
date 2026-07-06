# VERSIONS.md — Sankofa EHR version pins

**Single source of truth for version pins** for the Sankofa EHR distro (per master plan D2/§5, WP-001).
Every version below was verified against the cited URL on **2026-07-05**. Do not bump a version here without re-verifying against the source. Nothing in this file is guessed.

## 1. OpenMRS Platform

| Component | Pinned version | Source URL | Notes |
|---|---|---|---|
| OpenMRS core (platform) | **2.8.7** — the version RefApp 3.7.0 ships (per **ADR-010**, accepted 2026-07-06: the operative platform pin tracks the pinned stable RefApp release) | RefApp 3.7.0 `distro/pom.xml` (`openmrs.version` = 2.8.7, see §2) | For reference: latest 2.7.x patch is 2.7.9 (tag + Maven WAR verified; GitHub `releases/latest` misleadingly shows 2.7.6). `ghanaemr-core` declares `require_version 2.7.9` as its *minimum*, which 2.8.7 satisfies. |
| — Java requirement | **JDK 17+** (image uses Amazon Corretto 17) | https://raw.githubusercontent.com/openmrs/openmrs-core/2.7.9/pom.xml (enforcer rule `<jdk>[17, )</jdk>`); https://raw.githubusercontent.com/openmrs/openmrs-core/2.7.9/Dockerfile (`RUNTIME_JDK=jdk17-corretto`) | From the 2.7.9 build enforcer + official Docker image, not the wiki. |
| — Tomcat requirement | **Tomcat 9.0.x** (9.0.107 in official image) | https://raw.githubusercontent.com/openmrs/openmrs-core/2.7.9/Dockerfile (`FROM tomcat:9-jdk17-corretto`, `TOMCAT_VERSION=9.0.107`) | Official Docker image runs Tomcat 9 (javax servlet API). |
| openmrs-distro-platform | tag **platform-2.7.3** (latest 2.7.x tag; latest overall: platform-2.8.1) | https://api.github.com/repos/openmrs/openmrs-distro-platform/tags | Repo has no GitHub releases, tags only. Its Dockerfile just repackages `openmrs/openmrs-core` images; core 2.7.9 (above) is the authoritative platform pin. |

## 2. OpenMRS 3 Reference Application distro

| Component | Pinned version | Source URL | Notes |
|---|---|---|---|
| openmrs-distro-referenceapplication | **3.7.0** | https://api.github.com/repos/openmrs/openmrs-distro-referenceapplication/tags | Latest stable tag. `3.7.1-rc.1` exists (prerelease). Repo publishes tags only, no GitHub releases. |

Docker images used by its `docker-compose.yml` at tag 3.7.0 (https://raw.githubusercontent.com/openmrs/openmrs-distro-referenceapplication/3.7.0/docker-compose.yml):

| Service | Image | Notes |
|---|---|---|
| gateway | `openmrs/openmrs-reference-application-3-gateway:${TAG:-qa}` | Tag `3.7.0` confirmed on Docker Hub (pushed 2026-06-29). |
| frontend | `openmrs/openmrs-reference-application-3-frontend:${TAG:-qa}` | Tag `3.7.0` confirmed on Docker Hub (pushed 2026-06-29). |
| backend | `openmrs/openmrs-reference-application-3-backend:${TAG:-qa}` | Tag `3.7.0` confirmed on Docker Hub (pushed 2026-06-29). |
| db | `mariadb:10.11.7` | Hard-pinned in the RefApp compose file (older than latest 10.11.x — see §4). |

RefApp 3.7.0 platform + module pins, from https://raw.githubusercontent.com/openmrs/openmrs-distro-referenceapplication/3.7.0/distro/pom.xml: openmrs.version **2.8.7** (i.e. RefApp 3.7.0 runs core 2.8.x, not 2.7.x), initializer 2.12.0, fhir2 4.1.0, webservices.rest 3.5.0, idgen 5.0.4, addresshierarchy 2.21.0, attachments 4.0.0, queue 3.0.0, appointments 2.1.0-20250318.070530-1 (Bahmni SNAPSHOT build), reporting 2.1.0, reportingrest 2.0.0, stockmanagement 3.0.0, billing 2.3.0.

## 3. Backend modules (latest released versions)

Primary source: OpenMRS Maven repo `<release>` field in `https://mavenrepo.openmrs.org/public/org/openmrs/module/<artifact>-omod/maven-metadata.xml` (follow redirects), cross-checked against GitHub releases/tags.

| Module | Pinned version | Source URL | Notes |
|---|---|---|---|
| webservices.rest | **3.5.0** | Maven metadata (webservices.rest-omod); GitHub release 3.5.0 (2026-06-03) at openmrs/openmrs-module-webservices.rest | Matches RefApp 3.7.0. |
| fhir2 | **4.1.0** | Maven metadata (fhir2-omod); GitHub release 4.1.0 (2026-06-26) at openmrs/openmrs-module-fhir2 | Matches RefApp 3.7.0. |
| spa | **3.0.0** | Maven metadata (spa-omod); GitHub release `spa-3.0.0` (2026-06-30) at openmrs/openmrs-module-spa | |
| initializer | **2.12.0** | https://api.github.com/repos/mekomsolutions/openmrs-module-initializer/releases/latest (tag 2.12.0, 2026-06-09); Maven metadata (initializer-omod) agrees | Canonical repo is **mekomsolutions**/openmrs-module-initializer; no releases under the openmrs org. |
| idgen | **5.0.4** | Maven metadata (idgen-omod); tag 5.0.4 at openmrs/openmrs-module-idgen | No GitHub "latest release"; tags + Maven only. |
| datafilter | **2.2.0** | Maven metadata (datafilter-omod, lastUpdated 2023-04); tag 2.2.0 at openmrs/openmrs-module-datafilter | No release since 2023. |
| addresshierarchy | **2.21.0** | Maven metadata (addresshierarchy-omod); GitHub release 2.21.0 (2025-05-22) at openmrs/openmrs-module-addresshierarchy | |
| attachments | **4.0.0** | Maven metadata (attachments-omod); GitHub release 4.0.0 (2026-01-12) at openmrs/openmrs-module-attachments | |
| reporting | **2.1.0** | Maven metadata (reporting-omod); tag 2.1.0 at openmrs/openmrs-module-reporting | Requires calculation 2.0.0, htmlwidgets 2.0.1, serialization.xstream 0.3.0 (per RefApp 3.7.0 distro pom). |
| reportingrest | **2.0.0** | Maven metadata (reportingrest-omod); tag 2.0.0 at openmrs/openmrs-module-reportingrest | |
| queue | **3.0.0** | Maven metadata (queue-omod); GitHub release `queue-3.0.0` (2026-02-19) at openmrs/openmrs-module-queue | |
| stockmanagement | **3.0.0** | https://api.github.com/repos/openmrs/openmrs-module-stockmanagement/releases/latest (tag `stockmanagement-3.0.0`, 2026-02-19); Maven metadata (stockmanagement-omod) agrees | Repo now lives at **openmrs**/openmrs-module-stockmanagement; METS-Programme/openmrs-module-stockmanagement returns 404 (moved). |
| appointments (Bahmni) | **2.1.0** (released); RefApp 3.7.0 ships snapshot build `2.1.0-20250318.070530-1` | https://mavenrepo.openmrs.org/public/org/bahmni/module/appointments-omod/maven-metadata.xml (`<release>2.1.0</release>`); groupId `org.bahmni.module` per RefApp distro.properties | Repo bahmni/openmrs-module-appointments; its GitHub `releases/latest` is stale (1.1, 2018) — Maven is authoritative. |
| billing | **2.3.0** (note only — adoption decided in a later WP) | Maven metadata (billing-omod, lastUpdated 2026-06-04); tag v2.3.0 at openmrs/openmrs-module-billing | KenyaEMR/Palladium lineage, now under openmrs org. GitHub `releases/latest` is stale (v1.3.2). RefApp 3.7.0 uses 2.3.0. |

## 4. Database

| Component | Pinned version | Source URL | Notes |
|---|---|---|---|
| MariaDB 10.11 LTS | **10.11.18** | https://downloads.mariadb.org/rest-api/mariadb/10.11/ (highest release; dated 2026-05-28); Docker Hub tag `mariadb:10.11.18` confirmed via https://hub.docker.com/v2/repositories/library/mariadb/tags/10.11.18 | RefApp 3.7.0 compose still pins 10.11.7; we pin the latest 10.11.x patch. |

## 5. Tooling

| Component | Pinned version | Source URL | Notes |
|---|---|---|---|
| Docker Engine | **28.1.1** (build 4eba377) | Local: `docker --version` | Already installed locally; recorded as-is per WP-001. |
| Docker Compose | **v2.35.1** | Local: `docker compose version` | Already installed locally; recorded as-is. |
| Node.js LTS | **v24.18.0** ("Krypton", 2026-06-23) | https://nodejs.org/dist/index.json (latest entry with `lts` set) | Current LTS line is 24.x. NOTE: local machine currently has v21.1.0 (non-LTS) — needs upgrade in a later WP. |
| Caddy | **v2.11.4** | https://api.github.com/repos/caddyserver/caddy/releases/latest (2026-06-03) | Latest stable. |

## Verification log (2026-07-05)

- GitHub API `releases/latest` + `/tags` fetched for: openmrs-core, openmrs-distro-platform, openmrs-distro-referenceapplication, openmrs-module-{webservices.rest, fhir2, spa, idgen, datafilter, addresshierarchy, attachments, reporting, reportingrest, queue, stockmanagement, billing}, mekomsolutions/openmrs-module-initializer, bahmni/openmrs-module-appointments, caddyserver/caddy.
- Maven metadata (`<release>`) fetched from mavenrepo.openmrs.org for all modules above plus org.bahmni.module:appointments-omod and org.openmrs.api:openmrs-api (confirms core 2.7.9 and 2.8.7 released); openmrs-webapp-2.7.9.war existence confirmed (HTTP 200).
- RefApp 3.7.0: `docker-compose.yml`, `distro/distro.properties`, `distro/pom.xml` fetched from raw.githubusercontent.com at tag 3.7.0; Docker Hub tags `3.7.0` confirmed for frontend/backend/gateway images.
- openmrs-core 2.7.9 `pom.xml` (JDK enforcer) and `Dockerfile` (Corretto 17, Tomcat 9.0.107) fetched at tag 2.7.9.
- MariaDB releases from downloads.mariadb.org REST API; Node LTS from nodejs.org dist index; local Docker/Compose/Node via CLI.
- Nothing left unverified. Caveats: openmrs-core, bahmni appointments, and billing GitHub `releases/latest` entries are stale relative to tags/Maven (noted per row); METS-Programme stockmanagement repo is gone (404) — module now under the openmrs org.
