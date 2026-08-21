#!/usr/bin/env python3
"""Assert the Ghana address hierarchy matches the GSS structure: 16 regions,
261 MMDAs, no duplicates, no blank or comma-containing names (master plan
task 1.3; Appendix B says re-check counts against the current GSS list when
new districts are gazetted). Runs in CI. Exit 0 = valid."""
import collections
import sys
from pathlib import Path

EXPECTED_REGIONS = 16
EXPECTED_DISTRICTS = 261

csv_path = Path(__file__).resolve().parents[1] / (
    "distro/configs/openmrs/initializer_config/addresshierarchy/addresshierarchy.csv"
)
rows = [line.split(",") for line in csv_path.read_text().strip().splitlines()]

errors = []
if any(len(r) != 3 for r in rows):
    errors.append("rows without exactly 3 fields (Country,Region,District)")
if any(not all(f.strip() for f in r) for r in rows):
    errors.append("blank fields present")
if any(r[0] != "Ghana" for r in rows):
    errors.append("first field must be 'Ghana' on every row")

districts = [(r[1], r[2]) for r in rows]
dupes = [d for d, n in collections.Counter(districts).items() if n > 1]
if dupes:
    errors.append(f"duplicate districts: {dupes}")

regions = set(r[1] for r in rows)
if len(regions) != EXPECTED_REGIONS:
    errors.append(f"expected {EXPECTED_REGIONS} regions, found {len(regions)}: {sorted(regions)}")
if len(districts) != EXPECTED_DISTRICTS:
    errors.append(f"expected {EXPECTED_DISTRICTS} districts, found {len(districts)}")

if errors:
    print("ADDRESS HIERARCHY INVALID:")
    for e in errors:
        print(" -", e)
    sys.exit(1)
print(f"OK: {len(regions)} regions / {len(districts)} districts")
