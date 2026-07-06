# WP-020 — ghanaemr-core scaffold

| Field | Value |
| --- | --- |
| **Phase** | 1 — Weeks 3–10 · Core distro |
| **Status** | review |
| **Depends on** | WP-001 |
| **Estimate** | M (~1 day) — agent-assisted |

**Goal:** Scaffold the `ghanaemr-core` Maven OMOD with activator, config-flags pattern, §8 adapter interfaces with mocks + contract tests, and CI wiring.

**Context to load:**
- `docs/ghana-ehr-master-plan.md` §8 (integrations & interoperability)
- §11
- `docs/VERSIONS.md` (platform pin 2.7.9)
- Reference build files: `openmrs/openmrs-module-queue` @ tag `queue-3.0.0` (structure mirrored — see Sources in the progress log)

**Tasks:**
1. Create the Maven OMOD skeleton.
2. Add the activator.
3. Establish the config flags pattern.
4. Define adapter interfaces per §8 with mocks + contract tests.
5. Wire into CI.

**Out of scope:**
- Ghana Card **checksum** validation and identifier-type metadata/wiring (WP-012 — regex-only mock here, TODO left in `MockIdentityVerifier`).
- Consent/access service, network search, Data Filter mappings, audit table (WP-021/022/046).
- Real adapter implementations: NIA verification (Phase 5), NHIA eligibility (Phase 4+), Hubtel/Africa's Talking SMS (WP-072), claims file-export/API (WP-070/071).
- Distro integration — adding the omod to `distro/` module list (WP-010).
- Spotless/Checkstyle lint wiring (WP-008 CI lint stage).
- Liquibase changesets, custom entities, DAOs (no persistence needed yet).

**Acceptance criteria:**
- [ ] Module loads on the platform. *(Verified by the orchestrator after this WP: deploy `omod/target/ghanaemr.core-1.0.0-SNAPSHOT.omod` on the stock 3.7.0 refapp from WP-001 and confirm startup log + ping.)*
- [x] Sample REST ping resource works. *(`GET /ws/rest/v1/ghanaemr/ping` controller unit-tested; live check is part of the platform-load verification above.)*
- [x] ≥80% coverage on service code from the start. *(JaCoCo gate at 0.80 line coverage on the api module fails the build if violated; currently 100% api, 100% omod.)*

**Test plan:**
- JUnit 5 unit tests: activator (startup/shutdown logging), `GhanaemrConfig` (defaults, blank handling, on/true/off flag semantics, checksum flag), ping controller (mocked `ModuleFactory`, unknown-version branch, namespace).
- Contract tests: one abstract class per §8 adapter interface (`IdentityVerifierContractTest`, `InsuranceEligibilityContractTest`, `SmsGatewayContractTest`, `ClaimsSubmitterContractTest`) encoding null/blank handling, format rules, and result completeness; each mock's test extends its contract class so future real adapters reuse the same contracts.
- JaCoCo `check` (LINE ≥ 0.80) bound to `verify` on the api module.
- CI: `backend` job in `.github/workflows/ci.yml` runs `mvn -B clean verify` on temurin 17 with Maven caching.
- Result: 46 tests, 0 failures; dockerized `mvn clean package`/`verify` green.

**Artifacts:**
- `backend/ghanaemr-core/pom.xml` (+ `api/pom.xml`, `omod/pom.xml`) — structure mirrored from openmrs-module-queue @ queue-3.0.0; `openmrsPlatformVersion=2.7.9` per VERSIONS.md (ADR-010 may bump to 2.8.x).
- `backend/ghanaemr-core/api/src/main/java/org/openmrs/module/ghanaemr/` — `GhanaemrCoreActivator`, `GhanaemrConstants`, `api/GhanaemrConfig`, `adapter/*` (4 interfaces + 4 result types), `adapter/mock/*` (4 mock components).
- `backend/ghanaemr-core/omod/src/main/resources/config.xml` — module descriptor, requires webservices.rest, declares GP `ghanaemr.ghanacard.checksum=off`.
- `backend/ghanaemr-core/omod/src/main/java/.../web/GhanaemrPingRestController.java` — `/ws/rest/v1/ghanaemr/ping`.
- `backend/ghanaemr-core/README.md` — build, config-flags pattern, adapter table.
- `.github/workflows/ci.yml` — new `backend` job (validate job untouched).
- Built artifact: `backend/ghanaemr-core/omod/target/ghanaemr.core-1.0.0-SNAPSHOT.omod` (gitignored; reproducible via the dockerized build in the README).

## Progress log

- **2026-07-06** — Scaffold complete; status → review.
  - Mirrored `openmrs/openmrs-module-queue` @ `queue-3.0.0` build structure (standalone aggregator pom + api/omod submodules, maven-openmrs-plugin packaging, openmrs-test based testing); dropped its lombok/license/formatter plugins to keep the scaffold thin; skipped its `integration-tests` submodule.
  - Activator with startup/shutdown logging; config-flags pattern via `GhanaemrConfig` (global properties + defaults, `ghanaemr.ghanacard.checksum` default `off`); §8 adapter layer (IdentityVerifier, InsuranceEligibility, SmsGateway, ClaimsSubmitter) with mocks and abstract contract tests; Ghana Card checksum left as WP-012 TODO (regex `^GHA-\d{9}-\d$` only).
  - Ping REST controller registered the queue-module way (component-scanned `@Controller` extending `BaseRestController`). Full REST-framework integration testing judged disproportionate for a scaffold: controller is unit-tested; live verification happens at deployment.
  - Build verified green in Docker (`maven:3.9-eclipse-temurin-17`, local JDK is 11): 46 tests pass, JaCoCo 100% line coverage (api 92/92 lines, omod 10/10), coverage gate met, `.omod` produced and inspected (filtered config.xml, api jar under `lib/`).
  - **Remaining for orchestrator:** "module loads on platform" verification — deploy the omod on the WP-001 stock refapp (core 2.8.7 satisfies `require_version 2.7.9`) and hit `GET /ws/rest/v1/ghanaemr/ping`; then tick AC-1.
  - Sources consulted (all at `raw.githubusercontent.com/openmrs/openmrs-module-queue/queue-3.0.0/`): `pom.xml`, `api/pom.xml`, `omod/pom.xml`, `omod/src/main/resources/config.xml`, `omod/src/main/resources/webModuleApplicationContext.xml`, `api/src/main/resources/moduleApplicationContext.xml`, `api/src/main/java/.../QueueModuleActivator.java`, `omod/src/main/java/.../QueueEntryMetricRestController.java`, `omod/src/test/java/.../QueueEntryMetricRestControllerTest.java`; version pins from `docs/VERSIONS.md` (openmrs 2.7.9, webservices.rest 3.5.0).
