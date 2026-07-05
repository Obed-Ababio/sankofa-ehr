# Patient Consent Texts (v0)

> **DRAFT v0 — requires review by a Ghana-qualified lawyer and DPC verification, drafted 2026-07-05**
>
> Three short texts, each ≤150 words, written to be read aloud by staff. Signature or
> thumbprint lines on the printed versions. Twi/Ewe/Ga/Dagbani translations are a
> later deliverable (decision D12); English is v0. Consent capture flow per master plan
> §7 and WP-022.

## (a) Registration consent

> We will record your personal details and your health information in this clinic's
> computer system so that our staff can care for you, bill you correctly, and keep the
> records the law requires. Your information is kept private and secure. Only the
> people caring for you at this clinic will see it. Other clinics in our network cannot
> see your record unless you agree separately. You may ask to see or correct your
> information at any time at the front desk.
>
> Do you agree for this clinic to record and use your information in this way?
>
> ☐ Patient / guardian agrees — Signature or thumbprint: ______ Date: ______
> Staff witness: ______

*(~110 words read aloud)*

## (b) Cross-clinic record-sharing consent

*(Read aloud and signed/thumbprinted at the clinic being visited, before its staff can
open the record — master plan §7. The signed form may be photographed and attached to
the consent encounter.)*

> You are registered with another clinic in our network. With your permission, the
> staff of this clinic can see your existing health record — your past visits, test
> results, and medicines — so they can treat you safely today. If you agree, this
> clinic will have ongoing access to your record. You can withdraw this permission
> later by telling any clinic administrator in the network. If you say no, this clinic
> will still treat you, but will start a record without your past history.
>
> Do you agree for this clinic to see your health record?
>
> ☐ Patient / guardian agrees — Signature or thumbprint: ______ Date: ______
> Staff attestation: I read this to the patient and they agreed. ______

*(~115 words read aloud)*

## (c) SMS reminders opt-in (Phase 4)

> If you agree, we will send text messages to your phone number to remind you about
> appointments, medicine reviews, and vaccinations due for you or your child. The
> messages will not contain your test results or other private health details. Message
> receipt is free, but your network's normal charges may apply if you reply. You can
> stop the messages at any time by replying STOP or by telling the clinic front desk.
> Saying no will not affect your care.
>
> Do you agree to receive SMS reminders on [PHONE NUMBER]?
>
> ☐ Yes, send reminders ☐ No — Signature or thumbprint: ______ Date: ______

*(~95 words read aloud)*

---

**Implementation notes (not patient-facing):**
- (a) is captured at registration (WP-017); (b) creates the consent encounter and Data
  Filter mapping (WP-022); (c) sets the SMS opt-in flag with STOP handling (WP-073).
- Emergency "break-the-glass" access does not use text (b); it requires a typed reason,
  is audited, and is reported weekly to the network admin (master plan §7).
- Guardian consent applies for children and patients unable to consent; record the
  guardian's name and relationship on the form.
- Act 843 §20 requires prior consent for processing absent another lawful basis, and
  §31(5) contact details make accurate phone capture doubly important — keep (c)
  separate from (a); never bundle.
