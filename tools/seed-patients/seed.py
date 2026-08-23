#!/usr/bin/env python3
"""Seed synthetic Ghanaian patients and measure search latency (task 1.8).

Creates patients via the OpenMRS REST API with realistic names, phones,
region/district addresses, and identifier coverage mirroring reality
(~70% Ghana Card, ~60% NHIS; folder numbers always requested from Idgen —
the required identifier makes bare creates fail by design).

Usage:
  python3 seed.py --count 5000            # seed then measure
  python3 seed.py --count 200             # small dev run
  python3 seed.py --measure-only          # just the p95 report

Gate (Test Gate 1 item 3): with 5,000 patients, search p95 < 2s.
Exit code 1 if the gate fails.
"""
import argparse
import base64
import csv
import json
import random
import statistics
import sys
import time
import urllib.request
from concurrent.futures import ThreadPoolExecutor
from pathlib import Path

BASE = 'http://localhost'
AUTH = base64.b64encode(b'admin:Admin123').decode()
FOLDER_SOURCE = '305c9d92-51d0-49dd-8697-17ca15f4a35b'
OPENMRS_ID_SOURCE = '8549f706-7e85-4c1d-9424-217d50a2988b'
OPENMRS_ID_TYPE = '05a29f94-c0ed-11e2-94be-8c13b969e334'
GHANA_CARD_TYPE = 'ff994ca9-8813-4dfb-98b2-ab7b32064885'
NHIS_TYPE = '1b7af0ba-9ac0-48d4-a54d-cd6a84752fa3'
FOLDER_TYPE = '8e9059db-dc0c-4a87-8dd6-f018f1ca085d'
PHONE_ATTR = '14d4f066-15f5-102d-96e4-000c29c2a5d7'

MALE = ['Kwame', 'Kofi', 'Kwabena', 'Kwaku', 'Yaw', 'Kojo', 'Kwesi', 'Emmanuel', 'Daniel', 'Samuel',
        'Isaac', 'Joseph', 'Michael', 'Ebenezer', 'Prince', 'Richard', 'Eric', 'Stephen', 'Gabriel',
        'Abdul', 'Ibrahim', 'Mohammed', 'Yakubu', 'Seidu', 'Selorm', 'Elikem', 'Mawuli', 'Nii', 'Ato', 'Fiifi']
FEMALE = ['Ama', 'Akosua', 'Abena', 'Akua', 'Yaa', 'Adwoa', 'Afua', 'Esi', 'Efua', 'Grace',
          'Comfort', 'Mercy', 'Gifty', 'Patience', 'Vida', 'Josephine', 'Priscilla', 'Gloria', 'Agnes',
          'Hawa', 'Fatima', 'Amina', 'Zeinab', 'Dela', 'Sena', 'Enyonam', 'Naa', 'Adjoa', 'Maame', 'Ewurama']
SURNAMES = ['Mensah', 'Osei', 'Owusu', 'Boateng', 'Asante', 'Agyei', 'Appiah', 'Acheampong', 'Adjei',
            'Amoah', 'Antwi', 'Baah', 'Bonsu', 'Danso', 'Frimpong', 'Gyasi', 'Karikari', 'Kusi',
            'Nkrumah', 'Obeng', 'Ofori', 'Opoku', 'Sarpong', 'Yeboah', 'Addo', 'Annan', 'Aidoo',
            'Arthur', 'Baidoo', 'Eshun', 'Essel', 'Quainoo', 'Tetteh', 'Quartey', 'Lamptey', 'Ankrah',
            'Odoi', 'Sowah', 'Agbeko', 'Amevor', 'Attipoe', 'Dogbe', 'Kudzo', 'Tsikata', 'Abdulai',
            'Alhassan', 'Fuseini', 'Iddrisu', 'Mahama', 'Zakaria']
PHONE_PREFIXES = ['024', '025', '053', '054', '055', '059', '020', '050', '027', '026']

def api(path, payload=None, timeout=30):
    req = urllib.request.Request(
        f'{BASE}{path}',
        data=json.dumps(payload).encode() if payload is not None else None,
        headers={'Authorization': f'Basic {AUTH}', 'Content-Type': 'application/json'},
    )
    with urllib.request.urlopen(req, timeout=timeout) as r:
        return json.loads(r.read())

def load_districts():
    p = Path(__file__).resolve().parents[2] / 'configuration/addresshierarchy/addresshierarchy.csv'
    return [(r[1], r[2]) for r in csv.reader(p.read_text().splitlines())]

def make_patient(i, districts, used):
    female = random.random() < 0.52
    given = random.choice(FEMALE if female else MALE)
    surname = random.choice(SURNAMES)
    age_days = int(random.triangular(0, 90 * 365, 28 * 365))
    birthdate = time.strftime('%Y-%m-%d', time.localtime(time.time() - age_days * 86400))
    region, district = random.choice(districts)

    folder = api(f'/openmrs/ws/rest/v1/idgen/identifiersource/{FOLDER_SOURCE}/identifier',
                 {'comment': 'seed'})['identifier']
    openmrs_id = api(f'/openmrs/ws/rest/v1/idgen/identifiersource/{OPENMRS_ID_SOURCE}/identifier',
                     {'comment': 'seed'})['identifier']
    identifiers = [
        {'identifierType': FOLDER_TYPE, 'identifier': folder, 'preferred': True},
        {'identifierType': OPENMRS_ID_TYPE, 'identifier': openmrs_id},
    ]
    rec = {'folder': folder, 'name': f'{given} {surname}'}

    if random.random() < 0.70:
        pin = f'GHA-{700000000 + i:09d}-{random.randint(0, 9)}'
        identifiers.append({'identifierType': GHANA_CARD_TYPE, 'identifier': pin})
        rec['ghana_card'] = pin
    if random.random() < 0.60:
        nhis = f'{20000000 + i:08d}'
        identifiers.append({'identifierType': NHIS_TYPE, 'identifier': nhis})
        rec['nhis'] = nhis

    person = {
        'names': [{'givenName': given, 'familyName': surname}],
        'gender': 'F' if female else 'M',
        'birthdate': birthdate,
        'addresses': [{'country': 'Ghana', 'stateProvince': region,
                       'countyDistrict': district, 'cityVillage': district}],
    }
    if random.random() < 0.90:
        phone = random.choice(PHONE_PREFIXES) + f'{random.randint(0, 9999999):07d}'
        person['attributes'] = [{'attributeType': PHONE_ATTR, 'value': phone}]
        rec['phone'] = phone

    api('/openmrs/ws/rest/v1/patient', {'person': person, 'identifiers': identifiers})
    used.append(rec)

def seed(count):
    districts = load_districts()
    used, errors = [], []
    t0 = time.time()
    def one(i):
        try:
            make_patient(i, districts, used)
        except Exception as e:
            errors.append(str(e))
    with ThreadPoolExecutor(max_workers=6) as pool:
        for n, _ in enumerate(pool.map(one, range(count)), 1):
            if n % 500 == 0:
                rate = n / (time.time() - t0)
                print(f'  {n}/{count} ({rate:.1f}/s, ~{(count - n) / rate / 60:.1f} min left)')
    print(f'seeded {len(used)}/{count} in {(time.time() - t0) / 60:.1f} min; {len(errors)} errors')
    if errors:
        print('first errors:', errors[:3])
    return used

def measure(used):
    if not used:  # --measure-only: sample real patients for search terms
        res = api('/openmrs/ws/rest/v1/patient?q=a&v=custom:(display)&limit=50')
        used = [{'name': r['display'].split(' - ')[-1], 'folder': r['display'].split(' - ')[0]}
                for r in res['results']]
    kinds = {
        'name': lambda r: r['name'].split()[-1],
        'folder': lambda r: r.get('folder'),
        'ghana_card': lambda r: r.get('ghana_card'),
        'nhis': lambda r: r.get('nhis'),
        'phone': lambda r: r.get('phone'),
    }
    print('\nSearch latency (REST /patient?q=…), 40 samples each:')
    worst = 0.0
    for kind, get in kinds.items():
        terms = [t for t in (get(r) for r in random.sample(used, min(len(used), 400))) if t][:40]
        if not terms:
            print(f'  {kind:11} — no data to sample')
            continue
        times = []
        for t in terms:
            t1 = time.time()
            api(f'/openmrs/ws/rest/v1/patient?q={urllib.request.quote(t)}&limit=10')
            times.append((time.time() - t1) * 1000)
        p95 = statistics.quantiles(times, n=20)[18] if len(times) >= 20 else max(times)
        worst = max(worst, p95)
        print(f'  {kind:11} p50={statistics.median(times):6.0f}ms  p95={p95:6.0f}ms  max={max(times):6.0f}ms')
    print(f'\nGATE: worst p95 = {worst:.0f}ms — {"PASS (< 2000ms)" if worst < 2000 else "FAIL (>= 2000ms)"}')
    return worst < 2000

if __name__ == '__main__':
    ap = argparse.ArgumentParser()
    ap.add_argument('--count', type=int, default=5000)
    ap.add_argument('--base', default=BASE)
    ap.add_argument('--measure-only', action='store_true')
    args = ap.parse_args()
    BASE = args.base
    used = [] if args.measure_only else seed(args.count)
    sys.exit(0 if measure(used) else 1)
