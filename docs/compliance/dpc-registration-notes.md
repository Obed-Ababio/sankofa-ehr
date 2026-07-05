# DPC Registration — Research Notes (Data Processor)

> **DRAFT v0 — requires review by a Ghana-qualified lawyer and DPC verification, drafted 2026-07-05**

Scope: registering the Sankofa EHR network operator with Ghana's Data Protection
Commission (DPC) as a **data processor** under the Data Protection Act, 2012 (Act 843).
Each member clinic is the **data controller** for its patients and must hold (or obtain)
its own DPC registration — that obligation sits with the clinic and is flagged in the
clinic DPA and onboarding checklist.

All URLs below were fetched on **2026-07-05**. Statements without a citation are marked
UNVERIFIED.

## 1. Legal basis

- Act 843 §46–56 establish the Data Protection Register and the registration regime.
  The statutory text is written around "data controller" registration (§46(3): "A data
  controller shall register with the Commission"); §53 prohibits processing without
  registration and §56 makes failure to register an offence (fine up to 250 penalty
  units and/or up to 2 years imprisonment).
  Source: Act 843 full text, https://nita.gov.gh/wp-content/uploads/2017/12/Data-Protection-Act-2012-Act-843.pdf (accessed 2026-07-05; copy archived at `docs/sources/act-843-data-protection-act-2012.pdf`).
- In practice the DPC registers **both controllers and processors**: the Commission's
  site states "All data controllers and processors must register with the Commission",
  and its fee schedule prices "Data Controllers / Processors" classes together.
  Sources: https://dataprotection.org.gh/ and https://dataprotection.org.gh/registration/ (accessed 2026-07-05).
- §45 (Application of the Act): the Act applies where the controller is established in
  Ghana, or uses equipment or a data processor carrying on business in Ghana, or the
  processing concerns information originating partly or wholly from Ghana. A controller
  not incorporated in Ghana must register as an external company (§45(2)). Our clinics
  are Ghanaian entities; the operator entity should be a Ghanaian company before
  charging (master plan §16), which also makes the registration straightforward.

## 2. Portal and procedure (current, verified)

- **Main information page:** https://dataprotection.org.gh/registration/ (accessed 2026-07-05).
- **Registration/renewal portal:** https://app.dataprotection.org.gh/ — account creation
  at https://app.dataprotection.org.gh/auth/register, login/renewal at
  https://app.dataprotection.org.gh/auth/login. Both linked from the DPC homepage
  (accessed 2026-07-05). Note: the master plan's guess of
  `register.dataprotection.org.gh` is **not** the current portal; it is
  `app.dataprotection.org.gh`.
- The DPC launched new "Registration and Compliance Software" (RegSys) — announcement
  via Ministry of Communication: https://moc.gov.gh/data-protection-commission-launches-new-registration-and-compliance-software-and-announces-amnesty (surfaced in search 2026-07-05; page content not independently fetched).
- The registration page references downloadable **registration manuals** (PDF) on
  https://dataprotection.org.gh/registration/ — download and follow these when actually
  filing.

## 3. Required information (verified)

Act 843 §47(1) particulars (mirrored by the portal per the DPC registration page):

1. Business name and address of the applicant.
2. Representative name/address if an external company.
3. Description of personal data to be processed and categories of data subjects.
4. Whether special personal data will be held (for us: **yes** — health data is special
   personal data per the §96 interpretation of "special personal data", which includes
   "the physical, medical, mental health or mental condition or DNA of the data subject").
5. Purpose(s) of processing.
6. Recipients to whom data may be disclosed.
7. Countries to which data may be transferred — for us: the cloud hosting region per
   decision D6 (default candidate AWS af-south-1, Cape Town, South Africa) and the
   off-site backup region. State these explicitly.
8. Classes of persons whose data is held.
9. General description of security measures (summarize master plan §9 technical
   controls: TLS, encryption at rest, encrypted off-site backups, RBAC, audit logging,
   row-level clinic isolation, consent-gated cross-clinic access).
10. Any other information the Commission requires.

§47(2): knowingly supplying false information is an offence (fine up to 150 penalty
units and/or up to 1 year imprisonment). §47(3): separate Register entries per purpose
if data is kept for two or more purposes.

Source: Act 843 §47, archived text (above); https://dataprotection.org.gh/registration/ (accessed 2026-07-05).

## 4. Fee classes (verified — L.I. 2512, 2025 fee amendment)

The DPC homepage links the current fee schedule, an extract of the Fees and Charges
(Miscellaneous Provisions) (Amendment) Regulations, 2025 (**L.I. 2512**):
https://dataprotection.org.gh/wp-content/uploads/2026/02/DPC-NEW-FEES.pdf
(accessed 2026-07-05; copy archived at `docs/sources/dpc-fees-li-2512-2025.pdf`).

Registration fees (GHS):

| Class | Criteria | Fee (GHS) |
|---|---|---|
| Specialised industries | — | 5,000.00 |
| Large data controllers / processors | Annual turnover ≥ GH¢5 million **or** ≥250 members/staff; or specialist industries regardless of turnover (petroleum, telecom class-1, banking/financial, credit bureaus, insurance, mining); or group members with one qualifying affiliate | 2,340.00 |
| Medium data controllers or processors | Annual turnover above GH¢90,000 but below GH¢5 million, or max 50 staff / customers not above 249 | 1,170.00 |
| Small data controllers / processors | Annual turnover of GH¢90,000 and below, max 50 staff or customers | 156.00 |
| Certificate replacement | — | 38.00 |

Related fees in the same schedule: data protection supervisor training and
certification GHS 4,498; accreditation and certification of supervisor GHS 1,000; data
protection audits GHS 1,500/day.

**Our likely class at filing:** Small (pre-revenue) or Medium once subscription revenue
exceeds GH¢90,000/year — **UNVERIFIED how the DPC counts "customers" for a processor
serving clinics whose patients number in the thousands; confirm classification with the
DPC before paying.** Budget for Medium (GHS 1,170) to be safe.

Healthcare is **not** in the listed "specialist industries" secondary criterion, but
whether the DPC treats health-data processors as "Specialised Industries" for the
GHS 5,000 line is **UNVERIFIED — confirm with DPC**.

## 5. Validity and renewal (verified)

- Registration is renewed **every two years** (Act 843 §50; DPC materials describe a
  2-year Certificate of Registration).
- Renewal window per the DPC registration page: applications accepted **from 3 months
  before expiration until 7 days after**; renewal requires a compliance report and
  updated particulars. Source: https://dataprotection.org.gh/registration/ (accessed 2026-07-05).
- §55: changes to registered particulars must be notified to the Commission within
  **14 days**.

## 6. Timeline (realistic)

- Statutory signal: §57 (assessable processing) gives the Commission **28 days**
  (extendable by 14+ days) to respond where processing is "assessable" (categories to
  be specified by Executive Instrument). Whether health-data processing is currently
  designated assessable is **UNVERIFIED — confirm with DPC**.
- The DPC does not publish a standard processing SLA for ordinary registrations on the
  pages fetched. **UNVERIFIED — confirm with DPC.** Plan conservatively:
  account creation + form + payment ≈ 1 day of effort; certificate issuance
  ≈ 2–8 weeks. File in Week 1–2 per master plan §12 (Phase 0 exit: "DPC application
  drafted"); the pilot risk register already treats DPC delay as M×M with the
  mitigation "filed early; pilot proceeds with consent + DPA in place".

## 7. DPC contact points (from dataprotection.org.gh, accessed 2026-07-05)

- Email: info@dataprotection.org.gh, support@dataprotection.org.gh
- Phone: +233 25 630 1533 (main); 025 630 2031 (compliance); WhatsApp 050 617 7975
- Breach reporting page: https://dataprotection.org.gh/report-a-breach/

## 8. Open items before filing (human actions)

- [ ] Incorporate the Ghanaian operating entity (prerequisite for a clean registration;
      master plan §16).
- [ ] Confirm fee class (Small vs Medium vs "Specialised") with the DPC.
- [ ] Confirm whether processor registration uses the same portal flow and §47 form
      (expected yes, UNVERIFIED for processor-specific variations).
- [ ] Download and follow the current registration manual from the registration page.
- [ ] Decide Data Protection Supervisor designation (founder initially, per §9 of the
      master plan; note Act 843 §58 supervisors are optional appointments with
      DPC-set qualification criteria — formal certification costs GHS 4,498 per the fee
      schedule and is not a filing prerequisite as far as the fetched pages state;
      **UNVERIFIED — confirm with DPC**).
- [ ] Lawyer review of the completed application before submission.
- [ ] Each pilot clinic's own controller registration status checked during onboarding
      (add to WP-049 onboarding kit checklist).
