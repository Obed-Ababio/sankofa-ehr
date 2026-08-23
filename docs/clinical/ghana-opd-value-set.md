# Ghana OPD diagnosis value set — DRAFT for clinician review

**Status: DRAFT — not loaded into any system. Nothing here reaches the EMR until a
Ghanaian clinician signs off below (master plan task 2.1).**

This is the list of diagnoses the consultation form will offer clinicians at the
point of care (searchable dropdown, multiple diagnoses per visit, primary/secondary
rank). Every entry is a real concept from the [CIEL international dictionary](https://openmrs.atlassian.net/wiki/spaces/docs/pages/25470028/Getting+and+Using+the+CIEL+Concept+Dictionary)
with an ICD-10 mapping, so registers, NHIS claims and DHIMS2 reporting can be
generated without recoding. Free-text diagnoses are deliberately not allowed.

## What the reviewer should check

1. **Coverage** — does this list cover ~95% of what a Ghanaian private OPD sees in
   a normal week? What's missing? What would you never use (and should be cut)?
2. **Labels** — the *term* column is what front-line staff will search for. Where
   CIEL's official name differs, the CIEL name is in the note. Are the terms what
   a Ghanaian clinician would actually type?
3. **Granularity** — e.g. we list malaria as: Malaria / Confirmed / Presumed
   (suspected) / Severe / In pregnancy. Is that the right split for OPD + insurance
   + GHS reporting? Same question for the diabetes (type 1 / type 2 / unspecified)
   and malnutrition (SAM / MAM / kwashiorkor / marasmus) splits.
4. **The flagged rows** (see "Open questions") — each needs a clinical decision.

The list was seeded from the GHS top OPD causes (malaria, URTI, rheumatism/joint
pains, skin diseases, hypertension, diarrhoeal disease, acute eye infection,
intestinal worms, anaemia, typhoid, UTI, pneumonia, acute ear infection, diabetes,
asthma, dental caries, PID, accidents, pregnancy-related) plus the master plan's
Stage 2 list, then resolved concept-by-concept against the current CIEL release
(via a CIEL-loaded OpenMRS instance; see `tools/concepts/resolve-value-set.py`).

## Open questions for the reviewer

| # | Item | Question |
|---|---|---|
| 1 | **Cholera** (CIEL 122604) | CIEL currently has **no ICD-10 map** for the cholera diagnosis concept. Keep it (and we request the A00.9 map from CIEL upstream), or drop it as a referral/outbreak-only diagnosis? |
| 2 | **Fever, Dyspepsia, Joint pain, Myalgia** | These are symptom/finding concepts, not diagnoses. GHS OPD registers do use them as working diagnoses. Keep them (the form will need its diagnosis search widened to include them), or force clinicians to pick a proper diagnosis? |
| 3 | **Chronic suppurative otitis media** (CIEL 145151) | CIEL only has subtype concepts; we picked tubotympanic ("safe-type") CSOM. Right call? |
| 4 | **Hyperthyroidism** (CIEL 138176) | CIEL's only ICD-10 map is E05.5 (thyrotoxic crisis) where GHS would expect E05.9. Acceptable, or drop hyper-/hypothyroidism from OPD scope? |
| 5 | **Infertility** (CIEL 118293) | CIEL has no sex-neutral infertility diagnosis; we used *Female infertility*. Add *Male infertility* as a second entry? |
| 6 | **Pregnancy-induced hypertension** | CIEL's plain PIH concept (113859) has no ICD-10 map, so we used *Transient hypertension of pregnancy* (112275 → O13). Acceptable label? |
| 7 | **Referral diagnoses** (severe malaria, meningitis, sepsis, appendicitis, hernia, CVA, fracture, head injury, cataract, glaucoma, acute psychosis, pre-eclampsia) | Included so the OPD can record what it referred. Right set? |

## Sign-off

| | |
|---|---|
| Reviewed by (name, qualification) | |
| GMDC registration no. | |
| Date | |
| Verdict | ☐ Approved  ☐ Approved with changes (listed below)  ☐ Rejected |
| Changes requested | |

After sign-off: the approved rows become an OCL collection sourced from CIEL
(founder: this needs an OCL account — anonymous access was switched off in
2024/25), the collection export lands in `configuration/ocl/`, and the Initializer
loads it at startup. `resolve-value-set.py verify` then runs against the local
instance to prove every approved concept is present, unretired and ICD-mapped.

## The value set

The authoritative machine-readable copy is
[`ghana-opd-diagnoses-draft.csv`](ghana-opd-diagnoses-draft.csv) (same directory) —
the table below is generated from it. Columns: **term** is what staff will search;
**CIEL** is the concept ID in the international dictionary; **ICD-10** is the
mapped code (map type in parentheses where it is not an exact SAME-AS match).

<!-- BEGIN GENERATED TABLE (python3 tools/concepts/render-value-set-table.py) -->

### Malaria & febrile illness (7)

| Term | CIEL | CIEL name (if different) | ICD-10 | Note |
|---|---|---|---|---|
| Malaria | 116128 |  | B54 | GHS OPD cause #1 |
| Confirmed malaria | 160148 | Malaria, confirmed | B53.8 (narrower-than) | RDT/microscopy positive |
| Presumed malaria | 166623 | Suspected malaria | B54 (narrower-than) | CIEL name: Suspected malaria |
| Severe malaria | 160155 |  | B50.8 (narrower-than) | referral dx |
| Malaria in pregnancy | 134594 | Maternal Malaria During Pregnancy - Baby Not Yet Delivered | O98.6 (narrower-than) | CIEL name: Maternal malaria during pregnancy, baby not yet delivered |
| Fever | 140238 |  | R50.9 | pyrexia of unknown origin |
| Typhoid fever | 141 |  | A01.0 | GHS OPD top-20 |

### Infectious & parasitic (19)

| Term | CIEL | CIEL name (if different) | ICD-10 | Note |
|---|---|---|---|---|
| Cholera | 122604 | Cholera due to Vibrio cholerae | **none** | NO ICD-10 map in CIEL — clinician/CIEL request decision |
| Amebiasis | 122594 |  | A06.9 (narrower-than) |  |
| Giardiasis | 139457 |  | A07.1 |  |
| Ascariasis | 148353 |  | B77.9 |  |
| Intestinal helminthiasis | 116699 |  | B82.0 | GHS "intestinal worms" |
| Schistosomiasis | 117152 |  | B65.9 (narrower-than) | endemic (lakes/irrigation) |
| Onchocerciasis | 137393 | Infection by onchocerca volvulus | B73 | endemic (river basins) |
| Buruli ulcer | 146613 |  | A31.1 | Ghana endemic |
| Tuberculosis | 112141 |  | A16.9 (narrower-than) | all forms, per GHS register; CIEL pulmonary subtypes exist if needed |
| HIV disease | 138405 | Human immunodeficiency virus (HIV) disease | B24 |  |
| Measles | 134561 |  | B05.9 |  |
| Varicella | 892 |  | B01.9 | chickenpox |
| Mumps | 133671 |  | B26.9 |  |
| Pertussis | 114190 | Whooping cough | A37.9 | CIEL name: Whooping cough |
| Viral hepatitis B | 111759 | Hepatitis B | B16.9 | acute/unspecified hepatitis B |
| COVID-19 | 165623 | Disease due to severe acute respiratory syndrome coronavirus 2 (SARS-CoV-2) | U07.1 (narrower-than) |  |
| Influenza | 116958 |  | J11.1 (narrower-than) |  |
| Meningitis | 115835 |  | G03.9 | referral dx; northern belt |
| Sepsis | 126721 |  | A41.9 (narrower-than) | referral dx |

### Gastrointestinal (12)

| Term | CIEL | CIEL name (if different) | ICD-10 | Note |
|---|---|---|---|---|
| Gastroenteritis | 117889 |  | K52.9 (broader-than) | GHS "diarrhoea diseases" |
| Diarrhea | 142412 |  | A09.9 |  |
| Dysentery | 126558 | Shigellosis | A03.9 (narrower-than) | CIEL name: Shigellosis (bacillary dysentery); amoebic covered by Amebiasis |
| Food poisoning | 121272 | Bacterial food poisoning | A05.9 (narrower-than) | CIEL name: Bacterial food poisoning |
| Gastroesophageal reflux disease | 1293 |  | K21.9 |  |
| Gastritis | 110834 |  | K29.7 |  |
| Peptic ulcer disease | 114262 | Peptic ulcer | K27.9 (broader-than) |  |
| Dyspepsia | 111 |  | K30 (narrower-than) | ⚠ Symptom/Finding concept, not a Diagnosis |
| Constipation | 996 |  | K59.0 |  |
| Hemorrhoids | 138849 |  | I84.9 |  |
| Acute appendicitis | 149906 |  | K35.9 | referral dx |
| Inguinal hernia | 116954 |  | K40.9 (broader-than) | referral dx |

### Dental & oral (5)

| Term | CIEL | CIEL name (if different) | ICD-10 | Note |
|---|---|---|---|---|
| Oral candidiasis | 5334 | Candidiasis, oral | B37.0 (narrower-than) |  |
| Dental caries | 119558 |  | K02.9 | GHS OPD top-20 |
| Gingivitis | 139438 |  | K05.1 (broader-than) |  |
| Periodontitis | 130463 |  | K05.3 (broader-than) |  |
| Aphthous ulcer | 148531 | Aphthous ulceration | K12.0 | mouth ulcer |

### Respiratory (16)

| Term | CIEL | CIEL name (if different) | ICD-10 | Note |
|---|---|---|---|---|
| Upper respiratory tract infection | 149478 | Acute Upper Respiratory Infection | J06.9 | GHS OPD cause #2 |
| Common cold | 106 | Acute Coryza | J00 (narrower-than) | acute nasopharyngitis |
| Acute pharyngitis | 149579 |  | J02.9 |  |
| Acute tonsillitis | 149496 |  | J03.9 |  |
| Acute sinusitis | 121832 |  | J01.9 |  |
| Allergic rhinitis | 121692 |  | J30.4 |  |
| Acute otitis media | 149609 |  | H66.9 (narrower-than) | GHS "acute ear infection" |
| Chronic suppurative otitis media | 145151 | Chronic Tubotympanic Suppurative Otitis Media | H66.1 | tubotympanic (safe) CSOM — clinician to confirm subtype |
| Otitis externa | 114431 |  | H60.9 |  |
| Impacted cerumen | 530 | Cerumen impaction | H61.2 | very common ENT presentation |
| Pneumonia | 114100 |  | J18.9 |  |
| Acute bronchitis | 10 |  | J20.9 |  |
| Bronchiolitis | 121009 |  | J21.9 (broader-than) | paediatric |
| Asthma | 121375 |  | J45.9 |  |
| Acute exacerbation of asthma | 4 | Asthma exacerbation | J45.9 (narrower-than) |  |
| Chronic obstructive pulmonary disease | 1295 |  | J44.9 (narrower-than) |  |

### Cardiovascular (4)

| Term | CIEL | CIEL name (if different) | ICD-10 | Note |
|---|---|---|---|---|
| Essential hypertension | 140987 |  | I10 | GHS OPD top-5 |
| Congestive heart failure | 119910 |  | I50.0 |  |
| Angina pectoris | 121610 |  | I20.9 |  |
| Cerebrovascular accident | 111103 |  | I64 (narrower-than) | stroke; referral dx |

### Endocrine & metabolic (10)

| Term | CIEL | CIEL name (if different) | ICD-10 | Note |
|---|---|---|---|---|
| Diabetes mellitus type 2 | 142473 | Diabetes mellitus, type 2 | E11.9 |  |
| Diabetes mellitus type 1 | 142474 | Diabetes mellitus, type 1 | E10.9 |  |
| Diabetes mellitus | 119481 |  | E14.9 | unspecified — register category |
| Hypoglycemia | 138061 | Hypoglycemic Syndrome | E16.2 (narrower-than) | CIEL name: Hypoglycemic syndrome |
| Hyperlipidemia | 117441 |  | E78.5 |  |
| Goiter | 117772 |  | E04.9 (broader-than) |  |
| Hyperthyroidism | 138176 |  | E05.5 (narrower-than) | CIEL's only ICD map is E05.5 (thyrotoxic crisis) — GHS expects E05.9; clinician to advise |
| Hypothyroidism | 117321 |  | E03.9 (narrower-than) |  |
| Obesity | 115115 |  | E66.9 |  |
| Gout | 117762 |  | M10.9 |  |

### Nutrition (4)

| Term | CIEL | CIEL name (if different) | ICD-10 | Note |
|---|---|---|---|---|
| Severe acute malnutrition | 163302 |  | E63.9 (narrower-than) |  |
| Moderate acute malnutrition | 163303 |  | E63.9 (narrower-than) |  |
| Kwashiorkor | 116474 |  | E40 |  |
| Marasmus | 132636 | Nutritional marasmus | E41 | CIEL name: Nutritional marasmus |

### Blood (5)

| Term | CIEL | CIEL name (if different) | ICD-10 | Note |
|---|---|---|---|---|
| Iron deficiency anemia | 1226 | Anemia, iron deficiency | D50.9 (narrower-than) |  |
| Anemia | 121629 | Anaemia | D64.9 (narrower-than) | GHS OPD top-10; unspecified |
| Sickle cell anemia | 117703 | Sickle-cell anemia | D57.1 | high prevalence |
| Sickle cell crisis | 117643 | Hb-SS disease with crisis | D57.0 | CIEL name: Hb-SS disease with crisis |
| Glucose-6-phosphate dehydrogenase deficiency | 139387 | Glucose-6-Phosphate Dehydrogenase Deficiency Anemia | D55.0 | common in Ghana |

### Genitourinary (13)

| Term | CIEL | CIEL name (if different) | ICD-10 | Note |
|---|---|---|---|---|
| Urinary tract infection | 111633 |  | N39.0 (narrower-than) |  |
| Acute pyelonephritis | 149554 |  | N10 (narrower-than) |  |
| Gonorrhea | 117767 |  | A54.9 |  |
| Syphilis | 112493 |  | A53.9 (narrower-than) |  |
| Chlamydia | 120733 |  | A74.9 |  |
| Genital herpes | 117829 |  | A60.9 (broader-than) |  |
| Trichomoniasis | 117146 |  | A59.9 (narrower-than) |  |
| Vulvovaginal candidiasis | 146520 | Candidal Vulvovaginitis | B37.3 | CIEL name: Candidal vulvovaginitis |
| Bacterial vaginosis | 148002 |  | N76.0 |  |
| Pelvic inflammatory disease | 902 |  | N73.9 | GHS OPD top-20 |
| Sexually transmitted infection | 112992 | Sexually transmitted disease | A64 | CIEL name: Sexually transmitted disease |
| Benign prostatic hyperplasia | 121164 | Benign prostatic hypertrophy | N40 | CIEL name: Benign prostatic hypertrophy |
| Urolithiasis | 111628 | Urinary Calculus | N20.9 (narrower-than) | CIEL name: Urinary calculus; covers renal colic |

### Gynaecological (3)

| Term | CIEL | CIEL name (if different) | ICD-10 | Note |
|---|---|---|---|---|
| Dysmenorrhea | 118794 |  | N94.6 |  |
| Menorrhagia | 134345 | Hypermenorrhoea | N92.0 (narrower-than) |  |
| Infertility | 118293 | Female infertility | N97.9 | CIEL has no sex-neutral dx — female infertility; clinician to confirm |

### Pregnancy-related (7)

| Term | CIEL | CIEL name (if different) | ICD-10 | Note |
|---|---|---|---|---|
| Pregnancy | 152305 | Pregnancy confirmed | Z32.1 | CIEL name: Pregnancy confirmed |
| Pregnancy-induced hypertension | 112275 | Transient Hypertension of Pregnancy | O13 (broader-than) | = gestational HTN; plain PIH concept (CIEL 113859) has no ICD-10 map |
| Pre-eclampsia | 129251 |  | O14.9 | referral dx |
| Hyperemesis gravidarum | 490 |  | O21.9 (narrower-than) |  |
| Threatened abortion | 112416 |  | O20.0 |  |
| Anemia in pregnancy | 148834 | Anemia of Mother, Complicating Pregnancy, Childbirth, or the Puerperium, Unspecified as to Episode of Care | O99.0 | CIEL name: Anemia of mother complicating pregnancy |
| Mastitis | 134615 |  | N61 (narrower-than) |  |

### Musculoskeletal (9)

| Term | CIEL | CIEL name (if different) | ICD-10 | Note |
|---|---|---|---|---|
| Osteoarthritis | 114702 | Osteoarthrosis | M19.8 (broader-than) | CIEL name: Osteoarthrosis; GHS rheumatism & joint pains |
| Rheumatoid arthritis | 127417 |  | M06.9 |  |
| Arthralgia | 80 | Joint pain | M25.5 (narrower-than) | joint pain unspecified; ⚠ Finding concept, not a Diagnosis |
| Low back pain | 116225 |  | M54.5 |  |
| Neck pain | 133469 |  | M54.2 |  |
| Myalgia | 121 |  | M79.1 (narrower-than) | ⚠ Finding concept, not a Diagnosis |
| Sciatica | 127112 |  | M54.3 |  |
| Sprain | 112770 |  | T14.6 (narrower-than) |  |
| Fracture | 177 |  | T14.2 | referral dx |

### Skin & soft tissue (11)

| Term | CIEL | CIEL name (if different) | ICD-10 | Note |
|---|---|---|---|---|
| Cellulitis | 134 |  | L03.9 |  |
| Skin abscess | 150555 | Abscess of skin and subcutaneous tissue | L02.9 (narrower-than) |  |
| Impetigo | 137693 |  | L01.0 |  |
| Dermatophytosis | 119508 |  | B35.9 | tinea/ringworm |
| Scabies | 140 |  | B86 |  |
| Atopic dermatitis | 121348 | Atopic dermatitis and related condition | L20.9 | eczema |
| Contact dermatitis | 165961 |  | L25.9 (narrower-than) |  |
| Urticaria | 123468 |  | L50.9 |  |
| Acne vulgaris | 150126 |  | L70.0 |  |
| Herpes zoster | 117543 |  | B02.9 |  |
| Chronic ulcer of skin | 120551 | Chronic Skin Ulcer | L98.4 (broader-than) | tropical ulcer |

### Eye (7)

| Term | CIEL | CIEL name (if different) | ICD-10 | Note |
|---|---|---|---|---|
| Acute conjunctivitis | 149866 |  | H10.3 (narrower-than) | GHS "acute eye infection" |
| Allergic conjunctivitis | 995 | Conjunctivitis, allergic | H10.1 (narrower-than) |  |
| Cataract | 120860 |  | H26.9 (narrower-than) | referral dx |
| Refractive error | 119056 |  | H52.7 |  |
| Foreign body in eye | 113292 |  | T15.9 |  |
| Glaucoma | 117789 |  | H40.9 | referral dx |
| Hordeolum | 138434 |  | H00.0 (broader-than) | stye |

### Neurological (6)

| Term | CIEL | CIEL name (if different) | ICD-10 | Note |
|---|---|---|---|---|
| Headache | 139084 |  | R51 |  |
| Migraine | 115779 |  | G43.9 |  |
| Epilepsy | 155 |  | G40.9 |  |
| Febrile convulsion | 140485 |  | R56.0 | paediatric |
| Peripheral neuropathy | 118983 |  | G60.9 (narrower-than) | diabetic follow-up |
| Vertigo | 111525 |  | R42 (narrower-than) |  |

### Mental health (5)

| Term | CIEL | CIEL name (if different) | ICD-10 | Note |
|---|---|---|---|---|
| Depression | 119537 |  | F32.9 |  |
| Anxiety disorder | 121540 |  | F41.9 |  |
| Acute psychosis | 154937 | Acute and transient psychotic disorder | F23.9 | CIEL name: Acute and transient psychotic disorder; referral dx |
| Insomnia | 116743 |  | G47.0 (narrower-than) |  |
| Alcohol use disorder | 166221 | Mental or behavioral disorder due to alcohol use | F10.9 (narrower-than) |  |

### Injuries (9)

| Term | CIEL | CIEL name (if different) | ICD-10 | Note |
|---|---|---|---|---|
| Road traffic accident | 133924 | Motor Vehicle Accident (Victim) | V89.2 | CIEL name: Motor vehicle accident (victim); CIEL 86 carries a wrong ICD map (N25.8) — reported upstream |
| Head injury | 116838 |  | S09.9 | referral dx |
| Laceration | 136181 | Laceration of skin | T14.1 | open wound |
| Contusion | 119866 |  | T14.0 (narrower-than) | soft tissue injury |
| Burn | 116543 |  | T30.0 (narrower-than) |  |
| Dog bite | 166 | Bite, dog | T14.1 (narrower-than) | rabies exposure pathway |
| Snake bite | 126323 |  | T63.0 | rural clinics |
| Insect bite | 114795 | Insect Bites and Stings | T14.0 (narrower-than) | CIEL name: Insect bites and stings |
| Foreign body in ear | 139962 |  | T16 | common paediatric ENT |

### General (3)

| Term | CIEL | CIEL name (if different) | ICD-10 | Note |
|---|---|---|---|---|
| Dehydration | 142630 |  | E86 (narrower-than) |  |
| Allergic reaction | 121689 | Allergy | T78.4 (narrower-than) |  |
| Epistaxis | 133499 | Nasal haemorrhage | R04.0 (narrower-than) |  |
<!-- END GENERATED TABLE -->
