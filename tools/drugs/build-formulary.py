#!/usr/bin/env python3
"""Build the Sankofa drug formulary from the NHIS Medicines List (task 2.5).

Source data: nhis-ml-2025.csv (parsed from the NHIA March 2025 PDF, all 550
rows with NHIS code, generic, form, strength, price, prescribing level).
Scope: levels A/M/B1/B2/C — what a doctor-led (level C) facility may
prescribe; D and SM are excluded.

  resolve  -> docs/clinical/ghana-formulary-draft.csv
              Each in-scope row resolved to a CIEL drug concept + dosage-form
              concept via a CIEL-loaded OpenMRS (default: Bahmni demo — OCL's
              API is auth-only). pins.csv overrides name search per generic.
  emit     -> configuration/drugs/drugs-sankofa.csv
              + configuration/concepts/concepts-sankofa_2_drug_concepts.csv
              Initializer rows for every resolved draft entry, plus the
              referenced CIEL concepts (drug + dosage form) created under
              their canonical CIEL uuids — the local starter dictionary
              lacks most of them until full CIEL loads via OCL (2.1), and
              because the uuids are CIEL's, that later load updates these
              concepts in place instead of duplicating. Drug uuids are
              deterministic (uuid5 of the NHIS code) so re-runs never create
              duplicates. Unresolved rows are skipped and reported.

Usage:
  python3 build-formulary.py resolve
  python3 build-formulary.py emit
"""

import base64
import csv
import json
import os
import pathlib
import re
import sys
import time
import urllib.parse
import urllib.request
import uuid
from concurrent.futures import ThreadPoolExecutor

ROOT = pathlib.Path(__file__).resolve().parents[2]
SRC = pathlib.Path(__file__).parent / "nhis-ml-2025.csv"
PINS = pathlib.Path(__file__).parent / "pins.csv"
DRAFT = ROOT / "docs/clinical/ghana-formulary-draft.csv"
CONFIG = ROOT / "configuration/drugs/drugs-sankofa.csv"

BASE = os.environ.get("CIEL_BASE_URL", "https://demo.standard.mybahmni.in/openmrs").rstrip("/")
AUTH = os.environ.get("CIEL_AUTH", "admin:Admin123")
V = "custom:(uuid,display,retired,conceptClass:(name),names:(name))"
UUID_NS = uuid.UUID("6f7c8d9e-0a1b-4c2d-8e3f-405162738495")  # sankofa drug namespace

IN_SCOPE_LEVELS = {"A", "M", "B1", "B2", "C"}


def get(path):
    req = urllib.request.Request(f"{BASE}{path}")
    req.add_header("Authorization", "Basic " + base64.b64encode(AUTH.encode()).decode())
    for attempt in range(4):
        try:
            with urllib.request.urlopen(req, timeout=60) as res:
                return json.load(res)
        except Exception:
            if attempt == 3:
                raise
            time.sleep(2 ** attempt)


def ciel_id(u):
    m = re.fullmatch(r"(\d+)A+", u)
    return m.group(1) if m else None


def search(term, want_class):
    res = get(f"/ws/rest/v1/concept?q={urllib.parse.quote(term)}&limit=30&v={urllib.parse.quote(V)}")
    out = []
    for c in res["results"]:
        if c["retired"] or not ciel_id(c["uuid"]):
            continue
        if want_class and c["conceptClass"]["name"] != want_class:
            continue
        out.append(c)
    return out


def names_of(c):
    return [c["display"]] + [n["name"] for n in c.get("names") or []]


def variants(generic):
    """Name variants to try for combination products ('A + B' vs 'B + A')."""
    yield generic
    parts = [p.strip() for p in generic.split("+")]
    if len(parts) == 2:
        yield f"{parts[1]} + {parts[0]}"
        yield " / ".join(parts)
        yield " / ".join(reversed(parts))
        yield " and ".join(parts)


def resolve_generic(generic):
    for term in variants(generic):
        try:
            cands = search(term, "Drug")
        except Exception as e:
            return {"status": "error", "detail": str(e)}
        exact = next((c for c in cands if any(n.lower() == term.lower() for n in names_of(c))), None)
        if exact:
            return {"status": "exact", "ciel_id": ciel_id(exact["uuid"]), "concept_name": exact["display"]}
    # fall back to the best partial Drug-class hit on the original term
    try:
        cands = search(generic, "Drug")
    except Exception as e:
        return {"status": "error", "detail": str(e)}
    if cands:
        best = cands[0]
        return {"status": "review", "ciel_id": ciel_id(best["uuid"]), "concept_name": best["display"]}
    return {"status": "not-found"}


def resolve_form(form):
    try:
        cands = search(form, None)
    except Exception as e:
        return {"status": "error", "detail": str(e)}
    exact = [c for c in cands if any(n.lower() == form.lower() for n in names_of(c))]
    if exact:
        c = exact[0]
        return {"status": "exact", "ciel_id": ciel_id(c["uuid"]), "concept_name": c["display"],
                "concept_class": c["conceptClass"]["name"]}
    return {"status": "not-found"}


def load_pins():
    pins = {}
    if PINS.exists():
        for r in csv.DictReader(PINS.open()):
            pins[(r["kind"], r["term"])] = r
    return pins


def cmd_resolve():
    rows = [r for r in csv.DictReader(SRC.open()) if r["level"] in IN_SCOPE_LEVELS]
    pins = load_pins()

    def resolve_pinned(kind, term, resolver):
        pin = pins.get((kind, term))
        if pin:
            c = get(f"/ws/rest/v1/concept/{pin['ciel_id'].ljust(36, 'A')}?v={urllib.parse.quote(V)}")
            return {"status": "pinned", "ciel_id": pin["ciel_id"], "concept_name": c["display"]}
        return resolver(term)

    generics = sorted({r["generic"] for r in rows})
    forms = sorted({r["form"] for r in rows})
    with ThreadPoolExecutor(4) as pool:
        gmap = dict(zip(generics, pool.map(lambda g: resolve_pinned("generic", g, resolve_generic), generics)))
        fmap = dict(zip(forms, pool.map(lambda f: resolve_pinned("form", f, resolve_form), forms)))

    out = []
    for r in rows:
        g, f = gmap[r["generic"]], fmap[r["form"]]
        out.append({
            "nhis_code": r["nhis_code"], "generic": r["generic"], "form": r["form"],
            "strength": r["strength"], "level": r["level"], "price_ghs": r["price_ghs"],
            "unit_of_pricing": r["unit_of_pricing"],
            "drug_concept_id": g.get("ciel_id", ""), "drug_concept_name": g.get("concept_name", ""),
            "drug_concept_status": g["status"],
            "form_concept_id": f.get("ciel_id", ""), "form_concept_name": f.get("concept_name", ""),
            "form_concept_status": f["status"],
        })
    DRAFT.parent.mkdir(parents=True, exist_ok=True)
    with DRAFT.open("w", newline="") as fh:
        w = csv.DictWriter(fh, list(out[0].keys()), lineterminator="\n")
        w.writeheader()
        w.writerows(out)

    import collections
    gstat = collections.Counter(v["status"] for v in gmap.values())
    fstat = collections.Counter(v["status"] for v in fmap.values())
    print(f"{len(out)} formulations -> {DRAFT.relative_to(ROOT)}")
    print(f"generics ({len(generics)}): {dict(gstat)}")
    print(f"forms ({len(forms)}): {dict(fstat)}")
    for term, v in sorted(gmap.items()):
        if v["status"] in ("review", "not-found", "error"):
            print(f"  GENERIC {v['status']:9} {term}  ->  {v.get('concept_name', v.get('detail', ''))}")
    for term, v in sorted(fmap.items()):
        if v["status"] != "exact" and v["status"] != "pinned":
            print(f"  FORM    {v['status']:9} {term}  ->  {v.get('concept_name', '')}")
    return 0


CONCEPTS = ROOT / "configuration/concepts/concepts-sankofa_2_drug_concepts.csv"


def cmd_emit():
    rows = list(csv.DictReader(DRAFT.open()))
    ok, skipped, seen = [], [], {}
    for r in rows:
        if r["drug_concept_status"] not in ("exact", "pinned") or r["form_concept_status"] not in ("exact", "pinned"):
            skipped.append(r)
            continue
        # The NHIS list repeats a product per pack size (e.g. benzyl benzoate
        # 30 mL / 100 mL); OpenMRS drugs have no pack size — one row suffices.
        key = (r["generic"], r["form"], r["strength"])
        if key in seen:
            print(f"  DEDUPED {r['nhis_code']} (same product as {seen[key]})")
            continue
        seen[key] = r["nhis_code"]
        ok.append(r)
    CONFIG.parent.mkdir(parents=True, exist_ok=True)
    with CONFIG.open("w", newline="") as fh:
        w = csv.writer(fh, lineterminator="\n")
        w.writerow(["Uuid", "Void/Retire", "Name", "Concept Drug", "Concept Dosage Form", "Strength", "_version:1",
                    "_order:3003"])
        for r in ok:
            name = f"{r['generic']} {r['form']}" + (f", {r['strength']}" if r["strength"] else "")
            w.writerow([
                str(uuid.uuid5(UUID_NS, r["nhis_code"])), "", name,
                r["drug_concept_id"].ljust(36, "A"), r["form_concept_id"].ljust(36, "A"),
                r["strength"], "", "",
            ])

    # Create the referenced concepts under their CIEL uuids. Fetch each one's
    # canonical class/name from the CIEL instance so the local copies match.
    ids = sorted({r["drug_concept_id"] for r in ok} | {r["form_concept_id"] for r in ok}, key=int)

    def fetch(cid):
        c = get(f"/ws/rest/v1/concept/{cid.ljust(36, 'A')}?v={urllib.parse.quote(V)}")
        return {"id": cid, "name": c["display"], "class": c["conceptClass"]["name"]}

    with ThreadPoolExecutor(4) as pool:
        concepts = list(pool.map(fetch, ids))
    with CONCEPTS.open("w", newline="") as fh:
        w = csv.writer(fh, lineterminator="\n")
        w.writerow(["Uuid", "Void/Retire", "Fully specified name:en", "Data class", "Data type",
                    "Same as mappings", "_version:1", "_order:2500"])
        for c in concepts:
            w.writerow([c["id"].ljust(36, "A"), "", c["name"], c["class"], "N/A", f"CIEL:{c['id']}", "", ""])

    print(f"{len(ok)} drugs -> {CONFIG.relative_to(ROOT)}; {len(skipped)} skipped (unresolved)")
    print(f"{len(concepts)} referenced concepts -> {CONCEPTS.relative_to(ROOT)}")
    import collections
    print("concept classes:", dict(collections.Counter(c["class"] for c in concepts)))
    for r in skipped:
        print(f"  SKIPPED {r['nhis_code']} {r['generic']} ({r['drug_concept_status']}/{r['form_concept_status']})")
    return 0


if __name__ == "__main__":
    cmd = sys.argv[1] if len(sys.argv) > 1 else ""
    sys.exit(cmd_resolve() if cmd == "resolve" else cmd_emit() if cmd == "emit" else
             (print(__doc__), 2)[1])
