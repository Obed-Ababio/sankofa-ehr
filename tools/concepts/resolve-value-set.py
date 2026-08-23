#!/usr/bin/env python3
"""Resolve/verify the Ghana OPD diagnosis value set against a CIEL-loaded OpenMRS.

Task 2.1: the value set is curated as human-readable terms
(ghana-opd-terms.csv); every entry must correspond to a real, unretired CIEL
concept with an ICD-10-WHO mapping before it can go into the OCL collection.
OCL's API no longer allows anonymous access, so resolution runs against a
CIEL-loaded OpenMRS REST endpoint instead (default: the Bahmni standard demo,
which ships full CIEL — the OpenMRS dev3 dictionary is only a subset; point
CIEL_BASE_URL at this distro once CIEL is loaded locally).

A terms row may pin an exact concept with a `ciel_id` value; the resolver then
fetches that concept directly instead of trusting name search — used where
search picks a sibling concept (e.g. "Contusion" matching "Confusion").

  resolve  terms CSV  -> draft value-set CSV (ciel_id, name, ICD-10, status)
  verify   value-set CSV -> re-check every row (exists, name, class, ICD-10,
           not retired); exit 1 on any failure. Run before building the OCL
           collection, and again against the local instance after loading it.

Usage:
  python3 resolve-value-set.py resolve --terms ghana-opd-terms.csv --out draft.csv
  python3 resolve-value-set.py verify --set draft.csv
"""

import argparse
import base64
import csv
import json
import os
import re
import sys
import time
import urllib.parse
import urllib.request
from concurrent.futures import ThreadPoolExecutor

BASE = os.environ.get("CIEL_BASE_URL", "https://demo.standard.mybahmni.in/openmrs").rstrip("/")
AUTH = os.environ.get("CIEL_AUTH", "admin:Admin123")
V = "custom:(uuid,display,retired,conceptClass:(name),names:(name),mappings:(display,conceptMapType:(name)))"

FIELDS = ["category", "term", "ciel_id", "concept_name", "concept_class",
          "icd10", "icd10_map_type", "status", "flags", "note"]


def get(path):
    req = urllib.request.Request(f"{BASE}{path}")
    token = base64.b64encode(AUTH.encode()).decode()
    req.add_header("Authorization", f"Basic {token}")
    # The public demo servers drop connections under load — retry with backoff.
    for attempt in range(4):
        try:
            with urllib.request.urlopen(req, timeout=60) as res:
                return json.load(res)
        except Exception:
            if attempt == 3:
                raise
            time.sleep(2 ** attempt)


def ciel_id(uuid):
    m = re.fullmatch(r"(\d+)A+", uuid)
    return m.group(1) if m else None


def icd10_of(concept):
    """Best ICD-10-WHO mapping: prefer SAME-AS, then NARROWER-THAN."""
    maps = []
    for m in concept.get("mappings") or []:
        src, _, code = m["display"].partition(": ")
        if src == "ICD-10-WHO":
            maps.append((m["conceptMapType"]["name"], code))
    for want in ("SAME-AS", "NARROWER-THAN", "BROADER-THAN"):
        for map_type, code in maps:
            if map_type == want:
                return code, map_type
    return (maps[0][1], maps[0][0]) if maps else ("", "")


def fetch_concept(cid):
    return get(f"/ws/rest/v1/concept/{cid.ljust(36, 'A')}?v={urllib.parse.quote(V)}")


def resolve_term(row):
    term = row["term"]
    pinned = (row.get("ciel_id") or "").strip()
    out = {"category": row["category"], "term": term, "note": row.get("note", ""),
           "ciel_id": "", "concept_name": "", "concept_class": "", "icd10": "",
           "icd10_map_type": "", "status": "not-found", "flags": ""}

    def exact(c):
        names = [c["display"]] + [n["name"] for n in c.get("names") or []]
        return any(n.lower() == term.lower() for n in names)

    try:
        if pinned:
            best = fetch_concept(pinned)
            if best["retired"]:
                out.update(status="error", flags="pinned-concept-retired", ciel_id=pinned)
                return out
        else:
            res = get(f"/ws/rest/v1/concept?q={urllib.parse.quote(term)}&limit=25&v={urllib.parse.quote(V)}")
            live = [c for c in res["results"] if not c["retired"] and ciel_id(c["uuid"])]
            if not live:
                return out
            dx = [c for c in live if c["conceptClass"]["name"] == "Diagnosis"]
            best = (next((c for c in dx if exact(c)), None)
                    or next((c for c in live if exact(c)), None)
                    or (dx[0] if dx else live[0]))
    except Exception as e:
        out["status"], out["flags"] = "error", str(e)
        return out

    out["ciel_id"] = ciel_id(best["uuid"])
    out["concept_name"] = best["display"]
    out["concept_class"] = best["conceptClass"]["name"]
    out["icd10"], out["icd10_map_type"] = icd10_of(best)

    # ICD-10 map type is recorded but not flagged: CIEL routinely maps via
    # NARROWER-THAN and the code is still what the register/DHIMS2 export needs.
    flags = []
    if not pinned and not exact(best):
        flags.append("name-mismatch")
    if best["conceptClass"]["name"] != "Diagnosis":
        flags.append(f"class={best['conceptClass']['name']}")
    if not out["icd10"]:
        flags.append("no-icd10")
    out["flags"] = ";".join(flags)
    out["status"] = "pinned" if pinned else ("review" if flags else "exact")
    if pinned and flags:
        out["status"] = "review"
    return out


def cmd_resolve(args):
    with open(args.terms, newline="") as f:
        terms = list(csv.DictReader(f))
    with ThreadPoolExecutor(4) as pool:
        rows = list(pool.map(resolve_term, terms))

    seen = {}
    for r in rows:
        if r["ciel_id"]:
            if r["ciel_id"] in seen:
                r["flags"] = (r["flags"] + ";" if r["flags"] else "") + f"duplicate-of:{seen[r['ciel_id']]}"
                r["status"] = "review"
            else:
                seen[r["ciel_id"]] = r["term"]

    with open(args.out, "w", newline="") as f:
        w = csv.DictWriter(f, FIELDS)
        w.writeheader()
        w.writerows(rows)
    n = {s: sum(1 for r in rows if r["status"] == s) for s in ("exact", "review", "not-found", "error")}
    print(f"{len(rows)} terms -> {args.out}: {n['exact']} exact, {n['review']} need review, "
          f"{n['not-found']} not found, {n['error']} errors")
    return 0


def verify_row(row):
    problems = []
    try:
        c = fetch_concept(row["ciel_id"])
    except Exception as e:
        return [f"CIEL {row['ciel_id']} ({row['term']}): fetch failed: {e}"]
    if c["retired"]:
        problems.append("retired")
    names = [c["display"]] + [n["name"] for n in c.get("names") or []]
    if row["concept_name"].lower() not in (n.lower() for n in names):
        problems.append(f"name '{row['concept_name']}' not on concept (display: '{c['display']}')")
    if c["conceptClass"]["name"] != row["concept_class"]:
        problems.append(f"class {c['conceptClass']['name']} != {row['concept_class']}")
    icd10, map_type = icd10_of(c)
    if icd10 != row["icd10"]:
        problems.append(f"ICD-10 {icd10 or '(none)'} != {row['icd10']}")
    elif map_type != row["icd10_map_type"]:
        problems.append(f"ICD-10 map type {map_type} != {row['icd10_map_type']}")
    return [f"CIEL {row['ciel_id']} ({row['term']}): {p}" for p in problems]


def cmd_verify(args):
    with open(args.set, newline="") as f:
        rows = list(csv.DictReader(f))
    bad = [r for r in rows if not (r["ciel_id"] or "").isdigit()]
    for r in bad:
        print(f"FAIL: '{r['term']}' has no CIEL id")
    ids = [r["ciel_id"] for r in rows if r["ciel_id"]]
    dupes = {i for i in ids if ids.count(i) > 1}
    for d in sorted(dupes):
        print(f"FAIL: CIEL {d} appears {ids.count(d)}x")
    with ThreadPoolExecutor(4) as pool:
        problem_lists = list(pool.map(verify_row, [r for r in rows if (r["ciel_id"] or "").isdigit()]))
    problems = [p for pl in problem_lists for p in pl]
    for p in problems:
        print(f"FAIL: {p}")
    ok = not (bad or dupes or problems)
    print(f"{len(rows)} rows verified against {BASE}: " + ("all good" if ok else
          f"{len(bad) + len(dupes) + len(problems)} problem(s)"))
    return 0 if ok else 1


def main():
    ap = argparse.ArgumentParser(description=__doc__)
    sub = ap.add_subparsers(dest="cmd", required=True)
    p = sub.add_parser("resolve")
    p.add_argument("--terms", required=True)
    p.add_argument("--out", required=True)
    p = sub.add_parser("verify")
    p.add_argument("--set", required=True)
    args = ap.parse_args()
    sys.exit(cmd_resolve(args) if args.cmd == "resolve" else cmd_verify(args))


if __name__ == "__main__":
    main()
