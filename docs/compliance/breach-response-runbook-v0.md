# Breach Response Runbook (v0)

> **DRAFT v0 — requires review by a Ghana-qualified lawyer and DPC verification, drafted 2026-07-05**
>
> Covers suspected or confirmed unauthorised access, acquisition, loss, or disclosure
> of personal data held in Sankofa EHR. Solo-operator edition: one person fills all
> operator roles initially; the role split below is for when there is a team.

## Legal requirements (verified against Act 843 text, accessed 2026-07-05)

- **Act 843 §31(1)–(2):** where there are reasonable grounds to believe personal data
  has been accessed or acquired by an unauthorised person, **the data controller or a
  third party processing under its authority** must notify **(a) the Commission and
  (b) the data subject** — "as soon as reasonably practicable after the discovery".
  **The Act sets no fixed hour/day deadline** (no 72-hour rule in the statute text).
- **§31(3):** the controller must take steps to restore the integrity of the
  information system.
- **§31(4):** notification to the data subject may be delayed only if security
  agencies or the Commission advise it would impede a criminal investigation.
- **§31(5):** data-subject notification channels: registered mail, email, prominent
  website placement, media publication, or as the Commission directs.
- **§31(6)–(7):** the notice must give enough information for the data subject to take
  protective measures, including the identity of the unauthorised person if known.
- **§31(8):** the Commission may direct public disclosure of the compromise.
- **DPC current guidance on timelines:** the DPC's breach page
  (https://dataprotection.org.gh/report-a-breach/, accessed 2026-07-05) provides a
  report form and contacts but states **no specific deadline**. Any circulating
  "72-hour" figure is **UNVERIFIED — confirm current expectation with the DPC**.
  Our internal standard: DPC + affected clinics notified **within 72 hours** of
  confirmation, faster where feasible — this satisfies "as soon as reasonably
  practicable" on any reading.
- Source text: Act 843, https://nita.gov.gh/wp-content/uploads/2017/12/Data-Protection-Act-2012-Act-843.pdf (archived at `docs/sources/act-843-data-protection-act-2012.pdf`).

## Roles

| Role | Who (pilot) | Duties |
|---|---|---|
| Incident Lead | Founder | Runs the whole flow, owns decisions and the log |
| Technical Responder | Founder | Contain, investigate, restore |
| Comms Lead | Founder | Clinic, patient, DPC communications |
| Legal advisor | Retained Ghana counsel [NAME/PHONE — fill in] | Review notifications before sending |
| Clinic contact | Each clinic's administrator | Relays patient notifications, answers patient questions |

DPC contacts (accessed 2026-07-05): info@dataprotection.org.gh /
support@dataprotection.org.gh; +233 25 630 1533; breach form at
https://dataprotection.org.gh/report-a-breach/.

## Flow

### 0. Detect and log (T+0)

Triggers: alert (Grafana/Sentry/Uptime Kuma), audit-log anomaly, weekly
break-the-glass review, clinic report, researcher/third-party report.
Immediately open an incident log (timestamped, append-only — a dated file in the
private ops repo): who discovered, when, what is suspected. Start the clock; record
every action and decision with time.

### 1. Contain (target: within 1 hour of detection)

In rough priority order, as applicable:
- Disable compromised user accounts / rotate credentials and API tokens.
- Block offending IPs at Cloudflare; tighten WAF rules.
- Isolate the affected VM from the network (keep it running for evidence — see
  Evidence preservation) and fail over / restore service from clean infrastructure if
  needed.
- Revoke suspect Data Filter mappings or access grants.
- If backups are implicated, freeze the backup bucket (object lock already on).
- Do NOT delete anything. Do NOT reboot or reimage the affected host before evidence
  capture unless active harm is ongoing.

### 2. Assess (target: within 24 hours)

Establish, from audit tables (`ghanaemr_access_audit`), application logs, proxy logs,
and DB query logs:
- What data, which patients, which clinics, what time window, what vector.
- Whether data was **accessed or acquired by an unauthorised person** — this is the
  §31 notification trigger. If reasonable grounds exist, treat as notifiable; do not
  talk yourself out of it.
- Severity: S1 (confirmed clinical-data exfiltration) / S2 (confirmed unauthorised
  access, scope limited) / S3 (suspected, unconfirmed) / S4 (near-miss, no personal
  data exposed — not §31-notifiable; log and fix).
- Legal advisor engaged for S1–S3.

### 3. Notify (target: within 72 hours of confirmation; statute says "as soon as reasonably practicable")

Order of notifications:
1. **Affected clinics (controllers)** — within 24 hours of confirmation per DPA
   clause 8 (operator commits to 24h controller notification). Use Template A.
2. **Data Protection Commission** — jointly with or on behalf of clinics per the DPA;
   use the DPC breach form + email, and follow up by phone. Include: nature of the
   compromise, data categories and approximate numbers, clinics affected, containment
   and restoration steps taken (§31(3)), protective measures patients should take,
   identity of the intruder if known (§31(7)), contact point.
3. **Affected patients** — via their clinic, using Template B, through §31(5) channels
   (in practice: SMS/phone from the clinic, notice at the clinic, letter where
   addresses exist; website notice for large-scale incidents). Delay only per §31(4)
   on written advice of the DPC or security agencies — record that advice in the log.
4. **Status page** — factual service notice if availability was affected (no patient
   details ever).

### 4. Restore and close

- Restore system integrity (§31(3)): patch the vector, rotate all secrets, verify
  backups clean, re-run isolation invariant test suite before returning to service.
- Post-incident review within 7 days: timeline, root cause, what detection missed,
  corrective WPs filed. Share a summary with affected clinics.
- Retain the incident log and evidence bundle for at least [6 years — UNVERIFIED
  appropriate period, confirm with counsel].

## Evidence preservation

- Snapshot affected VM disks and take a memory image before any remediation that
  destroys state; store snapshots in a separate, access-restricted bucket.
- Export and hash (SHA-256) relevant logs: application, Caddy/Cloudflare, auth,
  MariaDB audit/binlogs, `ghanaemr_access_audit` extracts. Record hashes in the
  incident log.
- Preserve chain of custody: who captured what, when, stored where.
- Never work on originals; investigate on copies.
- If criminal conduct is suspected, engage counsel before contacting police; the DPC
  or security agencies may direct timing of patient notification (§31(4)).

## Template A — clinic notification (operator → clinic administrator/owner)

> Subject: [URGENT] Data security incident affecting [CLINIC NAME] patient data
>
> Dear [NAME],
> On [DATE/TIME] we identified [brief factual description]. Our current assessment is
> that [data categories] relating to approximately [N] of your patients [was accessed /
> may have been accessed] by an unauthorised party between [WINDOW].
> We have [containment actions taken]. Systems are [status].
> Under Act 843 §31 the Data Protection Commission and affected individuals must be
> notified as soon as reasonably practicable. We [have notified / will notify jointly
> with you by DATE] the Commission, and have prepared the attached patient notice for
> your review. Please call me to agree the patient notification plan.
> We will send updates every [12/24] hours until resolved.
> [NAME], [OPERATOR], [PHONE]

## Template B — patient notification (via clinic)

> [CLINIC NAME] — Important notice about your health information
>
> We are writing to tell you that on [DATE] some patient information held in our
> computer system was [accessed/taken] without permission. The information involved
> may include your [name / phone number / test results / other categories]. It did NOT
> include [categories not affected], and your money or bank details are not held in
> this system [adjust].
> What we have done: we stopped the unauthorised access, secured the system, and
> reported the matter to the Data Protection Commission.
> What you can do: [specific practical steps, e.g., be cautious of unexpected calls or
> messages claiming to be from the clinic; the clinic will never ask for passwords or
> payment by phone].
> If you have questions, speak to [CLINIC CONTACT] at the clinic or call [PHONE].
> We are sorry this happened and have taken steps to prevent it happening again.

## Drill

Run a tabletop exercise of this runbook before go-live (attach to WP-046/WP-060
checklists) and annually thereafter; log each drill like a restore drill.
