# Front desk SOP — search before create (duplicate guard)

*Task 1.5. There is no automated matching engine (registration app 6.1.0 has no
similar-patient check; ADR stance: revisit only if pilot data shows a real
duplicate rate). The guard is this SOP plus hard uniqueness on identifiers.*

## The rule

**Never register until you have searched.** Every duplicate folder creates a
split medical record — dangerous for the patient and expensive to merge.

## At check-in, in this order

1. **Ask for the Ghana Card** (most patients carry one; it is mandatory ID for
   transactions). Search by the PIN. If found → open the record, done.
2. No Ghana Card? **Ask for their phone number.** Search it (phones are
   searchable). Confirm name + age match before using the record.
3. Still nothing? **Search the name** (try spelling variants — Adjei/Agyei,
   Owusu/Owoso). Ask "have you ever been treated here before?"
4. Returning patient with a paper folder? Search the **legacy folder number**.
5. Only after all four come up empty → **register as new**. Capture the Ghana
   Card PIN and NHIS number if presented — the system rejects duplicates of
   both, which is the final safety net: if it says "already in use", the
   patient IS in the system — go back to search.

## What the system enforces for you

- Ghana Card PIN and NHIS number are unique — a second registration with the
  same number is rejected.
- The folder number is generated automatically — never type one at intake.
- Malformed Ghana Card / NHIS / phone values are rejected with an error.

## If you discover a duplicate after the fact

Do not delete anything. Note both folder numbers and report to the clinic
admin (merge is an admin action, tracked in the support channel).
