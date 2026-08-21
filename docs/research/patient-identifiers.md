# Patient identification for Sankofa EHR — research & recommendation

*2026-08-21. Informs Stage 1 task 1.1. Method: multi-agent web research (22 sources fetched, 25 claims extracted; 9 adversarially verified 3-0, the rest single-source quotes — confidence flagged per claim). Feeds the identifier-type config in `configuration/`.*

## 1. Ghana's ID landscape — what patients will present

**Ghana Card (NIA PIN)** — format `CCC-#########-#`: a 3-letter ICAO nationality code (`GHA` for citizens; non-citizen residents carry their nationality code, e.g. `NGA-…`), 9 digits, and a final check digit ([NIA-sourced discussion + regex](https://gist.github.com/roqkabel/f9a16815ac957d9124fd4ba6d4ffbd5b), corroborated by [LookupTax](https://lookuptax.com/docs/tax-identification-number/ghana-tax-id-guide)). NIA does not publish the check-digit algorithm → **format-only validation; no checksum verification**.
⚠️ *The master plan's `^GHA-\d{9}-\d$` excludes resident non-citizens — widen to `^[A-Z]{3}-\d{9}-\d$`.*

Legal status (verified 3-0 against [NIA's FAQ](https://nia.gov.gh/faqs/)): the Ghana Card is *mandatory for all transactions requiring proof of identity*, **but** it has *not* universally replaced other IDs — each institution decides individually. Practical consequence: expect high but incomplete presentation rates; voter IDs, driver's licences and passports will still appear at the front desk. The PIN doubles as the TIN since 2021.

**NHIS membership number** — the NHIA supports linking NHIS membership to the Ghana Card via `*929#`, and *"healthcare can be equally accessed with the Ghana card"* once linked (verified 3-0, [NHIA news](https://www.nhis.gov.gh/News/nhia-introduces-new-features-on-nhis-cards-5334)). So a patient may present *only* a Ghana Card for NHIS purposes. Research did **not** confirm the digit count of the membership number itself — the plan's 8-digit assumption stands but **must be checked against a current card** (unchanged week-2 caveat).

**Phone numbers** (all verified 3-0 against the [NCA National Numbering Plan](https://nca.org.gh/wp-content/uploads/2021/11/NUMBERING-PLAN-FOR-GHANA.pdf)): exactly 9 digits after the leading 0 (10 national digits, no variation). Initial digits `02`/`05` = mobile, `03` = fixed. Mobile Number Portability is operational (since 2011): prefix does not identify operator, numbers survive network switches, but are still shared/recycled → **phone is a search key and matching attribute, never a unique identifier**.

## 2. Best practice (single-source tier — verification pass was cut short; each claim quotes its source directly)

- **National IDs: optional-but-unique, never required.** Coverage is always partial in LMICs ([Nigeria NIN facility study](https://www.frontiersin.org/journals/digital-health/articles/10.3389/fdgth.2022.985337/full)); [OpenHIE's client-registry spec](https://guides.ohie.org/arch-spec/openhie-component-specifications-1/client-registry) is explicit that the canonical patient ID must be *system-assigned*, with national/insurance IDs as attached identifiers; [UNAIDS/PEPFAR guidance](https://www.unaids.org/sites/default/files/media_asset/JC2640_nationalhealthidentifiers_en.pdf) adds that record matching remains necessary even with a national ID.
- **MRN design:** include a check digit against transcription errors (UNAIDS). OpenMRS's native mechanism is a Luhn mod-N family ([wiki](https://openmrs.atlassian.net/wiki/spaces/docs/pages/25475698/Check+Digit+Algorithm) — base validator verified 3-0); the **Mod-30 charset is `0123456789ACDEFGHJKLMNPRTUVWXY`** (drops look-alikes B I O Q S Z) per the [idgen source](https://github.com/openmrs/openmrs-module-idgen/blob/master/api/src/main/java/org/openmrs/module/idgen/validator/LuhnModNIdentifierValidator.java). The [Idgen module](https://openmrs.atlassian.net/wiki/spaces/docs/pages/25461971/Idgen+Module) natively supports per-clinic prefixes + sequential generation, and applies the check digit when the identifier type carries the validator — the regex must include the check character.
- **Content-free caveat:** UNAIDS says identifiers should embed no information; our clinic prefix (`ACC-`) technically violates this. Accepted deliberately: paper-folder continuity and multi-clinic ops outweigh it at clinic tier; the prefix encodes *facility*, not patient attributes.
- **Duplicate prevention with partial ID coverage:** search-before-create SOP + the registration app's similar-patient check now; OpenHIE-style probabilistic matching/client registry only if pilot data shows real duplicate rates (matches plan task 1.5).
- **Act 843 (data protection):** patient records are *special personal data* (s.96); processing is lawful without consent for *medical purposes* by professionals under confidentiality (s.37(6)(e), 37(7)) — but storing the Ghana Card number is administrative, not medical, so keep it **optional and consent-based** ([Act 843 text](https://extranet.who.int/cpcd/health-legislation/data-protection-act-2012-act-843)). *Unverified legal reading — confirm with counsel/DPC during registration (task 0.6).*

## 3. Recommendation — identifier-type config for 1.1

| Identifier | Validation | Required | Unique | Generation | Notes |
|---|---|---|---|---|---|
| **Clinic folder number** (primary MRN) | `^[A-Z]{3}-\d{6}-[0-9ACDEFGHJKLMNPRTUVWXY]$` | **Yes** | Yes | Idgen `SequentialIdentifierGenerator`: per-clinic 3-letter prefix + zero-padded 6-digit sequence; **LuhnMod30 validator** on the type appends/validates check char | System-assigned canonical ID per OpenHIE. 6 digits = 1M patients/clinic headroom |
| **Ghana Card PIN** | `^[A-Z]{3}-\d{9}-\d$` | No | Yes | Manual entry | Widened from `GHA`-only to admit non-citizen residents. Format-only (no public checksum). Optional + consent flag (Act 843) |
| **NHIS membership no.** | `^\d{8}$` *(provisional)* | No | Yes | Manual entry | ⚠️ digit count unverified — **check a physical card**; note patients may present a linked Ghana Card instead |
| **Legacy folder no.** | none (free text) | No | No | Manual (Stage 4 importer) | Keeps paper folders findable |
| *Phone (person attribute, not identifier)* | `^0[235]\d{8}$` | — | No | Manual | NCA-derived: mobile 02/05 + fixed 03; searchable, never unique |

**In-country verification list:** NHIS digit count (physical card) · whether NIA will share the PIN check-digit algorithm (email info@nia.gov.gh) · clinic-prefix scheme agreement before first deployment.
