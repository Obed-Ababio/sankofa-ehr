# WP-006 — Brand & naming

| Field | Value |
| --- | --- |
| **Phase** | 0 — Weeks 1–2 |
| **Status** | in-progress |
| **Depends on** | — |
| **Estimate** | TBD — expand when picking up this WP |

**Goal:** Verify name availability and produce initial branding (logo placeholder, O3 theming tokens, favicon) that renders on the login page.

**Context to load:**
- `docs/ghana-ehr-master-plan.md` §13.3 entry; O3 theming docs

**Tasks:**
1. ~~Run the name availability sweep (trademark registry, domains, Ghana company names).~~ **Done (research half)** — see `docs/branding/naming-sweep.md`. Ghana ORC + trademark register are not publicly self-service searchable; those checks remain UNVERIFIED and need a manual/agent search once a name is shortlisted.
2. Create the logo placeholder. *(Blocked on name decision + WP-010 distro.)*
3. Define O3 theming tokens. *(Blocked on WP-010; theming applied in WP-029.)*
4. Create the favicon. *(Blocked on name decision + WP-010.)*

**Out of scope:** logo/theming execution before the distro exists (WP-010/WP-029); paid trademark filings.

**Acceptance criteria:**
- [ ] Branding config renders on the login page. *(Deliverable of the second half; depends on WP-010.)*

**Test plan:** TBD — expand when picking up this WP

**Artifacts:**
- `docs/branding/naming-sweep.md` — decision-ready naming memo (working name + 4 alternates, domain/collision/registry findings, comparison table, recommendation).
- `docs/sources/wp-006-sources.md` — sources with access dates.

**Progress log:**
- 2026-07-06 — Naming research complete. Domain sweep (DNS + RDAP) for .com/.org/.africa; .com.gh flagged UNVERIFIED (nic.gh WHOIS is captcha-gated; registry.nic.gh is registrar-login only). Collision findings: "Sankofa EHR" has no software collision but the word is heavily crowded in Ghana (2 herbal clinics, US Sankofa Health Center; sankofahealth.com/.org taken; sankofaehr.* likely available). "Akoma Health" eliminated (funded Lagos health-tech startup); "Ayaresa" high-risk (M'Ayaresa Health Tech, Ghana). Cleanest alternate: "Nkabom Health/EHR". Ghana ORC and trademark register: no public self-service search — marked UNVERIFIED, requires ORC/agent search before incorporation (with WP-052).
- **Remaining:** (1) founder decision on the name; (2) manual nic.gh, ORC, and RGD trademark searches for the chosen name; (3) logo/favicon/O3 theming tokens once WP-010 lands (login-page AC verified in WP-029 context).
