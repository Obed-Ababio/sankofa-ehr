# ghanaemr-core

Thin custom OpenMRS module (OMOD) for the Sankofa EHR distro — the only place backend
customization is allowed to live per master plan D2. Scaffolded in WP-020; later WPs add the
Ghana Card validator (WP-012), consent/access service (WP-022), and audit hooks (WP-046).

- **Module id:** `ghanaemr.core` · **Package:** `org.openmrs.module.ghanaemr`
- **Target platform:** OpenMRS core **2.7.9** (pinned in `docs/VERSIONS.md`; ADR-010 may move us
  to 2.8.x — the pin lives in the parent `pom.xml` property `openmrsPlatformVersion`)
- **Layout:** standard OpenMRS `api` + `omod` submodules, mirrored from
  `openmrs/openmrs-module-queue` at tag `queue-3.0.0`

## Build

The local machine only has JDK 11; OpenMRS 2.7.x needs JDK 17. Build with Docker (the named
volume caches the Maven repo between runs):

```sh
docker run --rm \
  -v "$(pwd)":/src -v ghanaemr-m2:/root/.m2 -w /src \
  maven:3.9-eclipse-temurin-17 mvn -B clean verify
```

`verify` runs all unit + contract tests and the JaCoCo coverage gate (>= 80% line coverage on the
`api` module; currently 100%). The deployable artifact lands at
`omod/target/ghanaemr.core-<version>.omod`. CI runs the same build in the `backend` job of
`.github/workflows/ci.yml`.

## Config-flags pattern

`org.openmrs.module.ghanaemr.api.GhanaemrConfig` (Spring component `ghanaemr.GhanaemrConfig`) is
the single accessor for `ghanaemr.*` settings. It reads OpenMRS **global properties** with
hard-coded defaults: unset/blank → default; a flag is enabled only when its value is
`on`/`true` (case-insensitive).

| Global property | Default | Meaning |
| --- | --- | --- |
| `ghanaemr.ghanacard.checksum` | `off` | Ghana Card check-digit validation. Stays off until WP-012 confirms the NIA algorithm and implements it. |

New features that are not deployment-ready ship behind a flag defaulting to `off`, declared as a
`globalProperty` in `omod/src/main/resources/config.xml` and exposed via a typed getter on
`GhanaemrConfig`.

## Adapter layer (master plan §8)

External integrations we do not yet have access to are hidden behind interfaces in
`org.openmrs.module.ghanaemr.adapter`, each with a mock implementation (Spring components in
`adapter.mock`) and an **abstract contract test** that any future real implementation must extend
and pass unchanged:

| Interface | Mock today | Real implementation |
| --- | --- | --- |
| `IdentityVerifier` (NIA Ghana Card) | `MockIdentityVerifier` — regex `^GHA-\d{9}-\d$` format check only, answers `UNVERIFIED`; checksum is a WP-012 TODO | Phase 5 (if NIA API access is obtained, D9) |
| `InsuranceEligibility` (NHIA membership) | `MockInsuranceEligibility` — always "Unverified — capture manually" | Phase 4+ |
| `SmsGateway` | `ConsoleSmsGateway` — logs instead of sending | Hubtel / Africa's Talking, Phase 4 (WP-072) |
| `ClaimsSubmitter` (NHIS claims) | `MockClaimsSubmitter` — refuses as `UNSUPPORTED` | File-export in WP-071, API slot reserved |

Contract tests live in `api/src/test/.../adapter/*ContractTest.java`; implementation tests extend
them (see `adapter/mock/*Test.java` for the pattern). If a future adapter lives in another module
(e.g. `ghanaemr-claims`), publish this module's test-jar first.

## REST

`GET /ws/rest/v1/ghanaemr/ping` → `{"module": "ghanaemr.core", "name": "Ghana EMR Core",
"version": "...", "status": "ok"}` — a scaffold health check proving custom REST registration
works (Spring `@Controller` extending `BaseRestController`, component-scanned via
`webModuleApplicationContext.xml`, same pattern as openmrs-module-queue). The controller is
unit-tested; live verification happens when the module is deployed on the platform (WP-010
distro assembly).
