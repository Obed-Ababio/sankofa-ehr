# WP-006 Naming sweep — "Sankofa EHR" and alternates

**Status:** research memo, v1 — input to a **founder decision**, not a decision.
**Date of all checks:** 2026-07-06. Domain availability from public DNS + RDAP (`rdap.org`) — registrars are authoritative; "likely available" means *no DNS resolution AND no RDAP registration record*, nothing more.
**Scope note:** this memo is the research half of WP-006 only. The other half (logo placeholder, O3 theming tokens, favicon, login-page branding config) **waits on the distro assembly (WP-010, theming applied in WP-029)** and is intentionally not done here.

## 0. Method

- **DNS:** `host <domain>` (NXDOMAIN noted).
- **RDAP:** `https://rdap.org/domain/<domain>` — HTTP 404 = no registration found; HTTP 200 = registered.
- **.gh / .com.gh:** there is **no public RDAP** for `.gh`. NIC Ghana's registry portal is live at <https://registry.nic.gh> (registrar **login** portal, `login.jsp` — not a public search) and <https://nic.gh> hosts a WHOIS lookup at `https://nic.gh/whois.php`, but it is **reCAPTCHA-gated and cannot be scripted** — a human must run the `.com.gh` checks there (or via an accredited registrar). DNS NXDOMAIN results for `.com.gh` below are indicative only.
- **Collisions:** web search (health/health-tech emphasis, Ghana emphasis). URLs + access dates in `docs/sources/wp-006-sources.md`.
- **Ghana company & trademark registers:** see §3 — largely **not publicly self-service searchable**; marked UNVERIFIED where applicable.

## 1. Candidates

Working name plus four alternates (Akan/Twi words with health- or network-relevant meaning, chosen for pronounceability by clinic staff and semantic fit with §2 of the master plan — "record follows the patient"):

| # | Candidate | Meaning |
|---|-----------|---------|
| A | **Sankofa EHR** (working name) | Adinkra: "go back and get it" — apt for retrieving patient records |
| B | **Nkabom Health / Nkabom EHR** | Akan: "unity / togetherness" — apt for a clinic *network* sharing one record |
| C | **Apomuden** | Twi: *apɔmuden* = "good health" |
| D | **Ayaresa** | Twi: "healing / medical treatment" (*ayaresabea* = hospital) |
| E | **Akoma Health** | *Akoma* = "heart" (Adinkra symbol of patience/tolerance) |

---

## 2. Candidate detail

### A. Sankofa EHR (working name)

**Domains (checked 2026-07-06):**

| Domain | DNS | RDAP | Verdict |
|---|---|---|---|
| sankofaehr.com | NXDOMAIN | 404 | **likely available** |
| sankofaehr.org | NXDOMAIN | 404 | **likely available** |
| sankofaehr.africa | NXDOMAIN | 404 | **likely available** |
| sankofahealth.com | NXDOMAIN | **200 (registered)** | taken (registered, not resolving) |
| sankofahealth.org | resolves (162.255.119.134) | **200 (registered)** | taken |
| sankofahealth.africa | NXDOMAIN | 404 | likely available |
| sankofaehr.com.gh / sankofa.com.gh | NXDOMAIN | n/a | **UNVERIFIED — check at nic.gh WHOIS (captcha-gated)** |

**Collisions (health / Ghana):** "Sankofa" is, as expected, a very common Ghanaian brand word. Notable:
- **Dr. Awuah Sankofa Natural Health Clinic**, Lapaz–Nii Boi Town, Accra — licensed Ghanaian *herbal/natural* clinic, rebranded to this name 2023 (drawuahsankofahealthclinic.com, accessed 2026-07-06). Health sector, Ghana — same country, adjacent sector.
- **Sankofa Herbal Store & Clinic**, Kumasi — herbal medicine/healthcare (ghanayello.com listing, accessed 2026-07-06).
- **Sankofa Health Center** (sankofahealthcenter.com, US) — culturally-grounded wellness/therapy center; holds the "Sankofa Health" framing in .com/.org space (accessed 2026-07-06).
- Non-health: the **Sankofa oil & gas field** (Eni/GNPC, offshore Ghana) dominates Ghanaian news usage of the word; numerous Sankofa-named NGOs, schools, and the 1993 film.
- **No existing "Sankofa EHR" or Sankofa-branded health-*software* product was found** (searched 2026-07-06).

**Practical notes:** Universally recognized and effortlessly pronounced by Ghanaian clinic staff; strong, apt meaning. Downsides: severe brand crowding → weak distinctiveness as a trademark in Ghana, and the two existing Sankofa *clinics* (herbal sector) create a small risk that "Sankofa" on a clinic's screens/printouts reads as affiliated with herbal medicine. The exact-match compound "SankofaEHR" is clean and its domains are open.

### B. Nkabom Health / Nkabom EHR

**Domains:**

| Domain | DNS | RDAP | Verdict |
|---|---|---|---|
| nkabomhealth.com | NXDOMAIN | 404 | **likely available** |
| nkabomhealth.org | NXDOMAIN | 404 | **likely available** |
| nkabomhealth.africa | NXDOMAIN | 404 | **likely available** |
| nkabom.com | resolves (parked-style, GoDaddy NS) | 200 (registered) | taken |
| nkabom.com.gh | NXDOMAIN | n/a | UNVERIFIED — nic.gh check |

**Collisions:** No health-tech product found. Existing users of the name (both Ghana-linked, neither a software/health product):
- **Nkabom Initiative** — Ghanaian youth-empowerment NGO with a *health-education* strand (nkabominitiative.com, accessed 2026-07-06).
- **Nkabom Collaborative** — McGill University / Mastercard Foundation higher-education agrifood programme with Ghanaian universities incl. University of Health and Allied Sciences (mcgill.ca, accessed 2026-07-06).

**Practical notes:** Meaning ("unity/togetherness") maps directly onto the core product promise — one record across a network of clinics. Easy to say (n-ka-BOM); Akan word so widely understood in southern/central Ghana, less self-explanatory in the north (minor). Cleanest collision picture of all candidates.

### C. Apomuden

**Domains:**

| Domain | DNS | RDAP | Verdict |
|---|---|---|---|
| apomuden.com | no resolution observed; NS = aveshost.net (Ghanaian host) | **200 (registered)** | taken |
| apomuden.org | NXDOMAIN | 404 | **likely available** |
| apomuden.africa | NXDOMAIN | 404 | **likely available** |
| apomuden.com.gh | NXDOMAIN | n/a | UNVERIFIED — nic.gh check |

**Collisions:** A GitHub **organization "Apomuden"** exists (github.com/Apomuden, accessed 2026-07-06 via API): 2 public repos (`api` — PHP, `webapp` — "Web app for apomuden"), no bio/website, appears **dormant**; sector unconfirmed but the name strongly suggests a (stalled) Ghanaian health project, consistent with apomuden.com being registered through a Ghanaian host. No live product, company, or marketing presence found.

**Practical notes:** The most literal name — *apɔmuden* simply means "good health" in Twi. Highly meaningful to staff and patients; four syllables (ah-po-mu-DEN), fine to say locally, harder for international audiences. The registered-but-idle .com and the dormant GitHub org are an annoyance, not a blocker — but the literalness that makes it apt also makes it weakly distinctive as a mark, and someone in Ghana clearly had the same idea already.

### D. Ayaresa

**Domains:**

| Domain | DNS | RDAP | Verdict |
|---|---|---|---|
| ayaresa.com | resolves — live site "**Ayaresa Food, Health, and Healing**" | 200 (registered) | taken (active, health-adjacent) |
| ayaresa.org | resolves (GoDaddy NS, no content title) | 200 (registered) | taken |
| ayaresa.africa | NXDOMAIN | 404 | likely available |
| ayaresa.com.gh | NXDOMAIN | n/a | UNVERIFIED — nic.gh check |

**Collisions — the notable one is in exactly our space:**
- **M'Ayaresa Health Tech** (mayaresa.health, accessed 2026-07-06) — Ghanaian health-tech startup (Ashesi University capstone origin) building digital solutions for hospital operations/patient workflows in Ghana. "Ayaresa" is fully contained in their name; confusion in the Ghanaian health-tech market is realistic.
- **ayaresa.com** — active "Food, Health, and Healing" site.

**Practical notes:** Beautiful fit semantically ("healing/treatment") and easy for staff, but the M'Ayaresa collision (same country, same sector, same buyer audience) makes this the second-riskiest candidate after Akoma. Not recommended without a trademark opinion.

### E. Akoma Health

**Domains:**

| Domain | DNS | RDAP | Verdict |
|---|---|---|---|
| akomahealth.com | resolves (15.197.148.33) | 200 (registered) | taken |
| akomahealth.org / .africa | NXDOMAIN | 404 | likely available (irrelevant, see below) |

**Collisions — HARD BLOCKER:**
- **Akoma Health** (akomahealth.io / akomahealth.com; LinkedIn, Crunchbase, PitchBook profiles, accessed 2026-07-06) — **active, VC-funded African health-tech startup**: teletherapy/mental-health platform, HQ Lagos, founded 2022, ~$1.03M raised (Google for Startups Black Founders Fund alumnus). Identical name, same continent, same broad sector.
- Also: Akoma Specialist Hospital, Akoma+ Health & Wellness, Akoma Homecare (various countries).

**Practical notes:** Eliminated. Direct name-for-name collision with a funded African digital-health company.

---

## 3. Ghana registries (companies & trademarks)

- **Company/business names — Office of the Registrar of Companies (ORC).** Current portal is **orc.gov.gh** (live 2026-07-06); it advertises "Entity Information Search" and "Name Search" services (orc.gov.gh/service/entity-information-search/). The linked e-portal, **egovonline.gegov.gov.gh** (the old RGD e-services host), was **unreachable (connection failure) on 2026-07-06**. There is **no confirmed free, public, self-service company-name search**; ORC searches are conventionally account/fee-based. → **UNVERIFIED — requires an ORC portal account or an agent search for each shortlisted name before incorporation** (master plan §16 requires incorporating before charging clinics, so this is on the critical path anyway, alongside WP-052).
- **Trademarks — Registrar General's Department (RGD) Trade Mark Registry.** rgd.gov.gh (Industrial Property page) is live but there is **no public online trademark database for Ghana's national register**. Pre-filing searches are conducted *at* the registry (recommended, not mandatory; fee-based, normally via an IP agent). International marks designating Ghana (Madrid Protocol) can be checked free in WIPO's Madrid Monitor, but that does not cover national filings. ARIPO (Banjul Protocol) marks likewise require a registry/agent search. → **UNVERIFIED — requires RGD/agent trademark search** for the chosen name (suggest Nice classes 9, 42, 44).
- **.gh domains — NIC Ghana.** registry.nic.gh is the live registry system (registrar login only); public WHOIS at nic.gh/whois.php is **captcha-gated** → the `.com.gh` column above is DNS-only and must be confirmed manually.

## 4. Comparison table

| Criterion | A. Sankofa EHR | B. Nkabom Health | C. Apomuden | D. Ayaresa | E. Akoma Health |
|---|---|---|---|---|---|
| .com | **likely avail** (sankofaehr.com) | **likely avail** (nkabomhealth.com) | taken (idle) | taken (active site) | taken (competitor) |
| .org | **likely avail** | **likely avail** | **likely avail** | taken | likely avail |
| .africa | **likely avail** | **likely avail** | **likely avail** | likely avail | likely avail |
| .com.gh | UNVERIFIED (DNS clear) | UNVERIFIED (DNS clear) | UNVERIFIED (DNS clear) | UNVERIFIED (DNS clear) | UNVERIFIED |
| Health collisions, Ghana | 2 herbal clinics (non-software) | none found | dormant GitHub org (unconfirmed) | **M'Ayaresa Health Tech (health-tech, Ghana)** | hospital namesakes |
| Health-tech collisions, global | none found | none found | none found | M'Ayaresa | **Akoma Health, Lagos, funded** |
| Meaning fit | strong ("retrieve the record") | strong ("one network") | strongest ("good health") | strong ("healing") | moderate ("heart") |
| Pronounceability (clinic staff) | excellent (universally known) | good | good locally | good | good |
| Distinctiveness as a mark | **weak** (most crowded word in Ghana branding) | good | weak–moderate (descriptive) | moderate | n/a |
| ORC / TM register | UNVERIFIED | UNVERIFIED | UNVERIFIED | UNVERIFIED | UNVERIFIED |
| Overall risk | medium (crowding, herbal association) | **low** | medium-low | high | **blocked** |

## 5. Recommendation (input to founder decision — not a decision)

1. **Eliminate:** **Akoma Health** (hard collision with a funded African health-tech company) and **Ayaresa** (direct-sector Ghanaian collision with M'Ayaresa Health Tech).
2. **Keepable:** the working name **Sankofa EHR** survives the sweep — no software/EHR collision found, and sankofaehr.com/.org/.africa are likely available. Its costs are strategic, not legal-on-their-face: extreme brand crowding in Ghana (weak distinctiveness, hard to ever enforce), and two existing Sankofa *herbal* clinics that could color perception with clinical customers.
3. **Strongest alternate:** **Nkabom Health / Nkabom EHR** — the only candidate with a clean collision picture *and* open .com/.org/.africa, and its meaning ("unity") arguably fits the network-record value proposition even better than Sankofa's.
4. **Dark horse:** **Apomuden** — best meaning, but .com is parked with a Ghanaian host and a dormant namesake GitHub org exists; fine as a product name if the founder accepts a non-.com primary domain.
5. **Whichever name is chosen, three verification steps are mandatory before money is spent on branding or incorporation** (all currently UNVERIFIED): (a) `.com.gh`/`.gh` check at nic.gh WHOIS (manual, captcha), (b) ORC company/business-name search (portal account or agent), (c) RGD trade-mark registry search (agent; classes 9/42/44). Registering the shortlist's gTLD domains now (~USD 10–30/yr each) is cheap optionality while the decision is pending.
6. **Deferred (rest of WP-006):** logo placeholder, favicon, and O3 theming tokens are blocked on the distro/frontend assembly (**WP-010**, theming in **WP-029**) and should be executed there once the name is locked, so the login-page AC can actually be demonstrated.

*Sources with access dates: `docs/sources/wp-006-sources.md`.*
