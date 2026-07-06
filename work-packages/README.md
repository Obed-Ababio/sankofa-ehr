# Sankofa EHR — Work Package Tracker

One file per work package, generated from the catalog in `docs/ghana-ehr-master-plan.md` §13
(template §13.1, exemplars §13.2, catalogs §13.3–13.8).

Each WP file follows the §13.1 template. Status values are **todo / in-progress / review / done**;
update the Status field in the WP file (and in this index) as work progresses. When a PR is opened
for a WP, link it in that WP's file. Fields marked "TBD — expand when picking up this WP" must be
expanded before implementation starts. Dependencies come from the catalog's "(dep …)" notes; "—"
means the catalog specifies none.

| WP | Title | Phase | Status | Depends on | File |
| --- | --- | --- | --- | --- | --- |
| WP-000 | Bootstrap | 0 | done | — | [WP-000-bootstrap.md](./WP-000-bootstrap.md) |
| WP-001 | Version pinning & stock spin-up | 0 | review | WP-000 | [WP-001-version-pinning.md](./WP-001-version-pinning.md) |
| WP-002 | Dev environment & Makefile | 0 | in-progress | WP-001 | [WP-002-dev-environment.md](./WP-002-dev-environment.md) |
| WP-003 | Accounts & secrets base | 0 | todo | — | [WP-003-accounts-secrets.md](./WP-003-accounts-secrets.md) |
| WP-004 | Region selection harness | 0 | in-progress | — | [WP-004-region-selection.md](./WP-004-region-selection.md) |
| WP-005 | Compliance kickoff (human+agent) | 0 | review | — | [WP-005-compliance-kickoff.md](./WP-005-compliance-kickoff.md) |
| WP-006 | Brand & naming | 0 | in-progress | — | [WP-006-brand-naming.md](./WP-006-brand-naming.md) |
| WP-007 | Staging host | 0 | todo | WP-003 | [WP-007-staging-host.md](./WP-007-staging-host.md) |
| WP-008 | CI/CD skeleton | 0 | todo | WP-001, WP-007 | [WP-008-cicd-skeleton.md](./WP-008-cicd-skeleton.md) |
| WP-009 | Backups v0 | 0 | todo | WP-007 | [WP-009-backups-v0.md](./WP-009-backups-v0.md) |
| WP-010 | Distro assembly | 1 | todo | WP-001 | [WP-010-distro-assembly.md](./WP-010-distro-assembly.md) |
| WP-011 | Metadata baseline | 1 | todo | WP-010 | [WP-011-metadata-baseline.md](./WP-011-metadata-baseline.md) |
| WP-012 | Identifier types & Ghana Card validation | 1 | todo | WP-010, WP-011, WP-020 | [WP-012-identifiers.md](./WP-012-identifiers.md) |
| WP-013 | Ghana address hierarchy | 1 | todo | WP-010 | [WP-013-address-hierarchy.md](./WP-013-address-hierarchy.md) |
| WP-014 | CIEL load | 1 | todo | WP-010 | [WP-014-ciel-load.md](./WP-014-ciel-load.md) |
| WP-015 | Ghana drug list | 1 | todo | WP-014 | [WP-015-ghana-drug-list.md](./WP-015-ghana-drug-list.md) |
| WP-016 | Lab catalog | 1 | todo | WP-014 | [WP-016-lab-catalog.md](./WP-016-lab-catalog.md) |
| WP-017 | Registration config | 1 | todo | WP-012, WP-013 | [WP-017-registration-config.md](./WP-017-registration-config.md) |
| WP-018 | Triage/vitals | 1 | todo | WP-014 | [WP-018-triage-vitals.md](./WP-018-triage-vitals.md) |
| WP-019 | OPD consult form | 1 | todo | WP-014, WP-016 | [WP-019-opd-consult-form.md](./WP-019-opd-consult-form.md) |
| WP-020 | ghanaemr-core scaffold | 1 | review | WP-001 | [WP-020-ghanaemr-core.md](./WP-020-ghanaemr-core.md) |
| WP-021 | Multi-clinic isolation | 1 | todo | WP-011, WP-020 | [WP-021-multi-clinic-isolation.md](./WP-021-multi-clinic-isolation.md) |
| WP-022 | Cross-clinic network search + consent flow | 1 | todo | WP-017, WP-020, WP-021 | [WP-022-network-search-consent.md](./WP-022-network-search-consent.md) |
| WP-023 | ANC forms | 1 | todo | WP-014 | [WP-023-anc-forms.md](./WP-023-anc-forms.md) |
| WP-024 | Immunization | 1 | todo | WP-014 | [WP-024-immunization.md](./WP-024-immunization.md) |
| WP-025 | Dispensing | 1 | todo | WP-015, WP-019 | [WP-025-dispensing.md](./WP-025-dispensing.md) |
| WP-026 | Lab results entry | 1 | todo | WP-016 | [WP-026-lab-results-entry.md](./WP-026-lab-results-entry.md) |
| WP-027 | Attachments | 1 | todo | WP-010 | [WP-027-attachments.md](./WP-027-attachments.md) |
| WP-028 | Printables | 1 | todo | WP-019, WP-025, WP-026 | [WP-028-printables.md](./WP-028-printables.md) |
| WP-029 | Theming & session polish | 1 | todo | WP-006, WP-010 | [WP-029-theming-session.md](./WP-029-theming-session.md) |
| WP-030 | Reports v1 | 1 | todo | WP-019 – WP-026 | [WP-030-reports-v1.md](./WP-030-reports-v1.md) |
| WP-031 | Seed & demo data | 1 | todo | WP-017 – WP-026 | [WP-031-seed-demo-data.md](./WP-031-seed-demo-data.md) |
| WP-032 | Stock management | 1 | todo | WP-015, WP-025 | [WP-032-stock-management.md](./WP-032-stock-management.md) |
| WP-033 | E2E suite | 1 | todo | most Phase 1 WPs above (see catalog) | [WP-033-e2e-suite.md](./WP-033-e2e-suite.md) |
| WP-034 | Perf baseline | 1 | todo | WP-033 | [WP-034-perf-baseline.md](./WP-034-perf-baseline.md) |
| WP-040 | Billing decision spike | 2 | todo | WP-010 | [WP-040-billing-spike.md](./WP-040-billing-spike.md) |
| WP-041 | Billing implementation | 2 | todo | WP-040 | [WP-041-billing-implementation.md](./WP-041-billing-implementation.md) |
| WP-042 | Service queues | 2 | todo | WP-019, WP-041 | [WP-042-service-queues.md](./WP-042-service-queues.md) |
| WP-043 | Appointments | 2 | todo | WP-010 | [WP-043-appointments.md](./WP-043-appointments.md) |
| WP-044 | Production environment | 2 | todo | WP-007, WP-008, WP-009 | [WP-044-production-environment.md](./WP-044-production-environment.md) |
| WP-045 | Observability | 2 | todo | WP-044 | [WP-045-observability.md](./WP-045-observability.md) |
| WP-046 | Security hardening pass | 2 | todo | WP-020, WP-044 | [WP-046-security-hardening.md](./WP-046-security-hardening.md) |
| WP-047 | DR finalization | 2 | todo | WP-044 | [WP-047-dr-finalization.md](./WP-047-dr-finalization.md) |
| WP-047a | Staging anonymizer | 2 | todo | — | [WP-047a-staging-anonymizer.md](./WP-047a-staging-anonymizer.md) |
| WP-048 | Training pack | 2 | todo | WP-033, WP-041, WP-042 | [WP-048-training-pack.md](./WP-048-training-pack.md) |
| WP-049 | Clinic onboarding kit | 2 | todo | — | [WP-049-onboarding-kit.md](./WP-049-onboarding-kit.md) |
| WP-050 | UAT with Clinic 1 | 2 | todo | WP-041 – WP-049 | [WP-050-uat-clinic-1.md](./WP-050-uat-clinic-1.md) |
| WP-051 | Data import tool | 2 | todo | — | [WP-051-data-import-tool.md](./WP-051-data-import-tool.md) |
| WP-052 | Legal pack final | 2 | todo | WP-005 | [WP-052-legal-pack-final.md](./WP-052-legal-pack-final.md) |
| WP-060 | Go-live Clinic 1 | 3 | todo | — | [WP-060-golive-clinic-1.md](./WP-060-golive-clinic-1.md) |
| WP-061 | Support system | 3 | todo | — | [WP-061-support-system.md](./WP-061-support-system.md) |
| WP-062 | Go-live Clinics 2–3 | 3 | todo | WP-060 (learnings) | [WP-062-golive-clinics-2-3.md](./WP-062-golive-clinics-2-3.md) |
| WP-063 | Pilot metrics | 3 | todo | WP-045 | [WP-063-pilot-metrics.md](./WP-063-pilot-metrics.md) |
| WP-064 | Iteration buffer | 3 | todo | — | [WP-064-iteration-buffer.md](./WP-064-iteration-buffer.md) |
| WP-065 | Post-pilot report | 3 | todo | — | [WP-065-post-pilot-report.md](./WP-065-post-pilot-report.md) |
| WP-070 | NHIS research pack | 4 | todo | — | [WP-070-nhis-research-pack.md](./WP-070-nhis-research-pack.md) |
| WP-071 | Claims module | 4 | todo | WP-070 | [WP-071-claims-module.md](./WP-071-claims-module.md) |
| WP-072 | Tariff sync tooling | 4 | todo | WP-071 | [WP-072-tariff-sync.md](./WP-072-tariff-sync.md) |
| WP-073 | SMS reminders | 4 | todo | WP-043 | [WP-073-sms-reminders.md](./WP-073-sms-reminders.md) |
| WP-074 | Analytics | 4 | todo | WP-044 | [WP-074-analytics.md](./WP-074-analytics.md) |
| WP-075 | Merge & dedup | 4 | todo | — | [WP-075-merge-dedup.md](./WP-075-merge-dedup.md) |
| WP-076 | Onboarding automation | 4 | todo | WP-062, WP-051 | [WP-076-onboarding-automation.md](./WP-076-onboarding-automation.md) |
| WP-080 | 50-clinic load proof | 5 | todo | WP-034 | [WP-080-load-proof.md](./WP-080-load-proof.md) |
| WP-081 | FHIR mirror (SHR-lite) | 5 | todo | WP-020 | [WP-081-fhir-mirror.md](./WP-081-fhir-mirror.md) |
| WP-082 | OpenHIM evaluation | 5 | todo | — | [WP-082-openhim-evaluation.md](./WP-082-openhim-evaluation.md) |
| WP-083 | Keycloak SSO + MFA | 5 | todo | — | [WP-083-keycloak-sso-mfa.md](./WP-083-keycloak-sso-mfa.md) |
| WP-084 | In-country hosting eval | 5 | todo | — | [WP-084-in-country-hosting.md](./WP-084-in-country-hosting.md) |
| WP-085 | External security assessment | 5 | todo | — | [WP-085-security-assessment.md](./WP-085-security-assessment.md) |
| WP-086 | Orchestration revisit | 5 | todo | — | [WP-086-orchestration-revisit.md](./WP-086-orchestration-revisit.md) |
| WP-087 | MoH/GHS engagement pack | 5 | todo | — | [WP-087-moh-engagement-pack.md](./WP-087-moh-engagement-pack.md) |
| WP-088 | Community release | 5 | todo | — | [WP-088-community-release.md](./WP-088-community-release.md) |
