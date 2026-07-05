# Retention Policy Memo — Health Records (v0)

> **DRAFT v0 — requires review by a Ghana-qualified lawyer and DPC verification, drafted 2026-07-05**

**To:** Project file (WP-005) · **From:** Network operator · **Re:** Retention of
patient health records in Sankofa EHR for Ghanaian private primary-care clinics.

## 1. Question

How long must/should member clinics (data controllers) retain patient health records
held in the system, and what does that mean for deletion behaviour in the product?

## 2. What Act 843 says (verified, accessed 2026-07-05)

Source: Act 843 text, https://nita.gov.gh/wp-content/uploads/2017/12/Data-Protection-Act-2012-Act-843.pdf (archived at `docs/sources/act-843-data-protection-act-2012.pdf`).

- **§24(1):** personal data shall not be retained longer than necessary for the
  purpose, **unless** (a) retention is required or authorised by law, (b) reasonably
  necessary for a lawful purpose related to a function or activity, (c) required by
  contract, or (d) the data subject consents to retention.
- **§24(4):** where a record is used to make a decision about the data subject (every
  clinical record qualifies), it must be kept for the period required by law or a code
  of conduct, or — absent one — long enough to afford the data subject an opportunity
  to request access.
- **§24(5)–(6):** at the end of the retention period, records must be destroyed,
  deleted, or de-identified, in a way that prevents reconstruction in intelligible
  form.
- **§25:** further processing must be compatible with the purpose of collection;
  §25(3)(d) permits further processing necessary to prevent or mitigate a serious and
  imminent threat to public health/safety or the life or health of the data subject.
- **§33(1)(b):** a data subject may request deletion of data the controller "no longer
  has the authorisation to retain" — i.e., deletion rights do not defeat lawful
  retention of clinical records.
- **§83:** a person shall not be *required* to produce records of an individual's
  physical/mental health made by or for a health professional in connection with care
  (a disclosure shield for health records in enforcement contexts).

Continuity of care is squarely a "lawful purpose related to a function or activity" of
a clinic (§24(1)(b)), and long-term retention of clinical records is the norm of
medical practice; the Act accommodates this.

## 3. Ghana-specific health-record retention periods

**UNVERIFIED — confirm with GHS/MoH and counsel.** As of 2026-07-05 I could not locate,
on public official websites, a current Ghana Health Service or Ministry of Health
directive stating minimum retention periods for private-clinic outpatient records
(searches across GHS/MoH/PRAAD sources returned no authoritative schedule). Items to
verify by human follow-up:

1. Whether GHS "Health Information Management" policy/SOPs prescribe retention periods
   for OPD folders, ANC registers, and immunization registers, and whether they bind
   private facilities (ask GHS HIM unit / the clinics' HeFRA licensing contacts).
2. Whether the Public Records and Archives Administration Act, 1997 (Act 535) /
   PRAAD retention schedules apply to private health facilities (likely public-sector
   only — confirm).
3. Any HeFRA (Health Facilities Regulatory Agency) licensing conditions on records
   retention for private clinics.
4. Professional-negligence limitation periods under Ghana's Limitation Act (NRCD 54)
   as a floor for defensive retention — counsel to advise (commonly cited as 3 years
   for tort claims, longer for minors; **UNVERIFIED**).
5. Common practice in comparable jurisdictions is 6+ years for adults and until
   majority + several years for children — useful as a sanity check only, not a Ghana
   citation.

## 4. Policy (default stance per master plan §9: retain, never silently delete)

Until the verifications in §3 land, and given §24(1)(b)/(c)/(d) provide lawful bases:

1. **Retain indefinitely by default.** Clinical records, consent records, and audit
   trails are retained for the life of the network. Rationale: continuity of care
   (the product's core promise), §24(1)(b) lawful purpose, contractual retention under
   the clinic Service Agreement/DPA (§24(1)(c)), and patient consent at registration
   (§24(1)(d)).
2. **Never silently delete.** No automated purge of clinical data. Any deletion is a
   deliberate, logged, human-approved act (e.g., a lawful §33 correction/deletion
   request, or a controller exit under DPA clause 10) — and voiding, not physical
   row deletion, is used inside the EMR wherever the platform supports it.
3. **Deletion requests (§33):** honoured for data the clinic is no longer authorised
   to retain (e.g., erroneous registrations, duplicate merges); refused with reasons
   for clinical records lawfully retained — the request and outcome are documented and
   the patient told they may complain to the DPC.
4. **Consent revocation ends access, not the record.** Cross-clinic mapping is
   end-dated; the record and the audit history of past access are retained (master
   plan §7).
5. **Clinic exit:** full export to the departing controller, then delete/de-identify
   per DPA clause 10, subject to the shared-record carve-out and audit-trail
   retention. §24(6)-compliant destruction (unrecoverable) for anything deleted.
6. **De-identified data** for statistics/research falls under §24(2)–(3) (retention
   permitted with protection); staging environments use anonymized data only
   (WP-047a).
7. **Backups:** 35-day rolling cycle; deletion propagates to backups by expiry, not by
   rewrite (documented in the DPA).
8. **Revisit trigger:** this memo is updated the moment verified GHS/MoH/HeFRA
   guidance or DPC direction arrives, and at minimum at WP-052 (legal pack final) with
   lawyer input. If a verified mandatory *maximum* retention period ever emerges,
   product work to implement scheduled, logged disposal will be scoped as a new WP.

## 5. Product implications (for later WPs)

- No TTL/purge jobs on clinical tables (WP-046 hardening checklist should assert
  this).
- Void-with-reason patterns everywhere; physical deletion tooling restricted to the
  network admin with dual confirmation and audit (WP-075 merge tooling follows the
  same rule).
- Export tooling (FHIR/CSV) is a contractual guarantee (master plan §16) — WP-051/
  WP-081 dependencies.
