#!/usr/bin/env python3
"""Regenerate the value-set table inside docs/clinical/ghana-opd-value-set.md
from ghana-opd-diagnoses-draft.csv. Run after any resolve that changes the CSV."""

import csv
import pathlib
import re

root = pathlib.Path(__file__).resolve().parents[2]
csv_path = root / "docs/clinical/ghana-opd-diagnoses-draft.csv"
md_path = root / "docs/clinical/ghana-opd-value-set.md"

rows = list(csv.DictReader(csv_path.open()))
lines = []
category = None
for r in rows:
    if r["category"] != category:
        category = r["category"]
        n = sum(1 for x in rows if x["category"] == category)
        lines.append(f"\n### {category} ({n})\n")
        lines.append("| Term | CIEL | CIEL name (if different) | ICD-10 | Note |")
        lines.append("|---|---|---|---|---|")
    icd = r["icd10"] or "**none**"
    if r["icd10"] and r["icd10_map_type"] != "SAME-AS":
        icd += f" ({r['icd10_map_type'].lower()})"
    ciel_name = r["concept_name"] if r["concept_name"].lower() != r["term"].lower() else ""
    note = r["note"]
    if "class=" in r["flags"]:
        cls = re.search(r"class=([^;]+)", r["flags"]).group(1)
        note = (note + "; " if note else "") + f"⚠ {cls} concept, not a Diagnosis"
    lines.append(f"| {r['term']} | {r['ciel_id']} | {ciel_name} | {icd} | {note} |")

text = md_path.read_text()
begin, end = "<!-- BEGIN GENERATED TABLE (python3 tools/concepts/render-value-set-table.py) -->", "<!-- END GENERATED TABLE -->"
head, _, rest = text.partition(begin)
_, _, tail = rest.partition(end)
md_path.write_text(head + begin + "\n" + "\n".join(lines) + "\n" + end + tail)
print(f"{len(rows)} rows across {len({r['category'] for r in rows})} categories -> {md_path.relative_to(root)}")
