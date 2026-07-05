# Data Processing Agreement — Template (v0)

> **DRAFT v0 — requires review by a Ghana-qualified lawyer and DPC verification, drafted 2026-07-05**
>
> Annex to the Sankofa EHR Service Agreement (WP-052 finalizes both). Act 843 §30(2)
> requires processing for a controller by a processor to be governed by a written
> contract, and §30(3) requires that contract to oblige the processor to maintain
> confidentiality and security measures. This template implements that requirement.
> Square brackets = fill at signing.

---

## Data Processing Agreement

Between **[CLINIC LEGAL NAME]** ("the Clinic", data controller) and
**[OPERATOR LEGAL NAME]** ("the Operator", data processor), together "the Parties",
made under the Data Protection Act, 2012 (Act 843) of Ghana.

### 1. Subject matter

The Operator provides the Clinic with a hosted electronic health record service
("Sankofa EHR") and processes personal data on the Clinic's behalf solely to deliver
that service. The Clinic is and remains the data controller for the personal data of
its patients and staff; the Operator is a data processor.

### 2. Duration

This Agreement runs concurrently with the Service Agreement dated [DATE] and remains in
force for as long as the Operator processes personal data for the Clinic, plus the
wind-down period in clause 10.

### 3. Nature and purpose of processing

Hosting, storage, display, transmission, backup, and support of electronic health
records and related operational data; generation of the Clinic's reports; preparation
of NHIS claim exports at the Clinic's direction; and, where separately consented by the
patient, making a patient's record available to another network clinic under the
network consent model described in Annex A.

### 4. Categories of data and data subjects

- **Data subjects:** the Clinic's patients (including minors), their guardians/next of
  kin where recorded, and the Clinic's staff users.
- **Personal data:** identification data (name, date of birth, sex, address, phone,
  Ghana Card number, NHIS number, medical record numbers); visit, billing, and payment
  data; staff account and activity data.
- **Special personal data (Act 843 §96 definition includes physical, medical, mental
  health condition and DNA):** clinical data — complaints, examinations, diagnoses,
  test orders and results, prescriptions and dispensing, immunizations, antenatal care
  records, referrals, and attached documents/images.

### 5. Clinic (controller) obligations

The Clinic shall:
1. Register and maintain registration with the Data Protection Commission as a data
   controller (Act 843 §46(3), §53) and notify the DPC of relevant changes (§55).
2. Ensure a lawful basis (including patient consent where required, §20, §37) for the
   data it collects, and provide patients the information required by §27(2) (the
   privacy notice supplied with the service may be used for this).
3. Capture cross-clinic sharing consent using the agreed consent flow before requesting
   network access to a patient record, and use the emergency override only for genuine
   emergencies.
4. Keep its user accounts personal and confidential (no shared logins), deactivate
   leavers promptly, and use the system only for patient care and clinic administration.
5. Handle patient access/correction requests (§32, §33, §35) at first instance, with
   the Operator's assistance under clause 6.7.

### 6. Operator (processor) obligations

The Operator shall:
1. Process personal data only on the Clinic's documented instructions as embodied in
   the service's functions and this Agreement, and only with the Clinic's prior
   knowledge or authorisation (§29(1)(a)).
2. Treat all personal data as confidential (§29(1)(b)) and not disclose it except as
   required by law or in discharge of a duty (§29(2)).
3. Establish, maintain, and continually update the confidentiality and security
   measures required by §28 and §30(3), including at minimum: encryption in transit
   and at rest; encrypted off-site backups; role-based access control; per-clinic
   row-level data isolation; audit logging of access grants, emergency overrides, and
   administrative actions; and the further controls in Annex B (security summary).
4. Maintain its own DPC registration as a data processor.
5. Notify the Clinic of any security compromise per clause 8.
6. Not engage a sub-processor other than those disclosed in clause 7 without prior
   written notice to the Clinic, and bind all sub-processors to obligations no less
   protective than this Agreement.
7. Assist the Clinic, within a reasonable time, in responding to data subject requests
   (access, correction, consent withdrawal) and DPC inquiries.
8. Ensure personnel with access to personal data (initially the Operator's principal
   only) are bound by confidentiality obligations.
9. Keep personal data complete, accurate and available to the Clinic as entered (§26
   support), including through tested backups (recovery point objective ≤ 15 minutes,
   recovery time objective ≤ 4 hours, per the Service Agreement service levels).

### 7. Sub-processors and data location

1. Personal data is hosted with **[CLOUD PROVIDER]** in the **[REGION — per decision
   D6; default candidate AWS af-south-1, Cape Town, South Africa]** region. Encrypted
   backups are stored with **[BACKUP STORAGE PROVIDER, REGION(S), including the
   cross-region backup copy]**.
2. Ancillary sub-processors (e.g., content delivery/security: [CLOUDFLARE], error
   monitoring: [SENTRY], metrics: [GRAFANA CLOUD], SMS gateway when enabled:
   [HUBTEL / AFRICA'S TALKING]) are listed with their functions and data exposure in
   Annex C. Monitoring/error services are configured not to receive patient clinical
   data.
3. Data is processed outside Ghana. The Operator warrants that such processing
   complies with the laws of Ghana as required for foreign processing arrangements
   (cf. §30(4)) and will cooperate with any future DPC localization guidance,
   including migration to in-country hosting if required (Operator roadmap item).

### 8. Breach notification

1. On becoming aware of reasonable grounds to believe personal data has been accessed
   or acquired by an unauthorised person, the Operator shall notify the Clinic
   **without undue delay, and in any event within 24 hours** of becoming aware.
2. Act 843 §31 requires notification of the **Commission and the data subject** "as
   soon as reasonably practicable after the discovery"; the Parties shall cooperate so
   the required notifications are made per the breach-response runbook (Annex D). As
   controller, the Clinic is the primary notifying party; §31(1) also places the duty
   on a third party processing under the controller's authority, so the Operator may
   notify the DPC directly where the Clinic is unreachable or agrees.
3. The Operator shall take steps to restore the integrity of the affected system
   (§31(3)), preserve evidence, and provide the Clinic all information reasonably
   needed for notifications, including the identity of the unauthorised person if
   known (§31(7)).
4. Notification to data subjects shall be delayed only where security agencies or the
   Commission advise that notification will impede a criminal investigation (§31(4)).

### 9. Audit rights

1. The Operator shall keep records of its processing activities and security measures
   sufficient to demonstrate compliance with this Agreement.
2. Once per year, or after a material security incident, the Clinic (or the DPC, or an
   independent auditor bound to confidentiality) may on 14 days' notice audit the
   Operator's compliance, by written questionnaire, review of the Operator's audit
   logs and security documentation, or — where reasonably necessary — inspection.
   Multi-clinic audits may be consolidated; the Operator may share a common audit
   report with all member clinics.
3. The Operator shall provide each clinic a weekly report of emergency-override
   ("break-the-glass") accesses affecting its patients.

### 10. Data return and deletion on exit

1. **Full-export guarantee.** At any time during the Agreement and on termination, the
   Operator shall, on request and without additional charge beyond reasonable media
   costs, provide the Clinic a complete export of the personal data the Clinic
   controls, in open, machine-readable formats (FHIR R4 resources and/or CSV, plus
   attached files in original formats). This clause survives termination.
2. On termination, the Operator shall provide the clause-10.1 export, then — subject
   to clause 10.3 — delete or de-identify the Clinic's data from production systems
   within 90 days, with backup copies expiring per the backup retention cycle (35
   days) thereafter. Deletion shall prevent reconstruction in an intelligible form
   (cf. §24(6)).
3. **Shared-record carve-out.** Where a patient's record is also lawfully controlled
   by another member clinic (the patient registered or consented there), the record
   remains in the system under that clinic's controllership; only the departing
   Clinic's controller relationship and access are ended. Audit trails and consent
   histories are retained as required for legal accountability (§24(1)(a)–(b)).
4. Health records shall not be destroyed where retention is required by law or
   applicable health-records guidance (see the network retention policy memo).

### 11. Liability, term, general

[Cross-refer to the Service Agreement's liability, indemnity, governing-law (Ghana),
and dispute-resolution clauses. Lawyer to draft.]

---

**Annexes**
- **Annex A** — Network consent model (from `docs/consent-model.md` / master plan §7).
- **Annex B** — Security measures summary (from master plan §9 technical controls).
- **Annex C** — Sub-processor list (provider, function, region, data exposure).
- **Annex D** — Breach-response runbook (`docs/compliance/breach-response-runbook-v0.md`).
- **Annex E** — Consent texts and privacy notice as provided to patients.

Signed for the Clinic: ______________ Date: ______
Signed for the Operator: ______________ Date: ______
