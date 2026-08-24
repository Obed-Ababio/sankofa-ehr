#!/usr/bin/env bash
# Create the dev/test users for each Sankofa role. Idempotent — safe to
# re-run; 400 "already exists" responses are ignored.
# Production users are created per-clinic at go-live (docs/security/roles.md);
# credentials below are for local/CI use only.
set -u
BASE="${1:-http://localhost}"
AUTH="admin:Admin123"

create_user() { # username password given family role_uuid
  local payload
  payload=$(cat <<JSON
{
  "username": "$1",
  "password": "$2",
  "person": {"names": [{"givenName": "$3", "familyName": "$4"}], "gender": "F"},
  "roles": [{"uuid": "$5"}]
}
JSON
)
  code=$(curl -s -o /tmp/create-user-out.json -w "%{http_code}" -u "$AUTH" \
    -X POST "$BASE/openmrs/ws/rest/v1/user" \
    -H 'Content-Type: application/json' -d "$payload")
  if [ "$code" = "201" ]; then echo "created $1"; else echo "$1: HTTP $code (already exists?)"; fi
}

create_user frontdesk  Frontdesk123  Efua  Frontdesk  c39a1f5e-8a11-4a3e-9f01-6ff8f7d10001
create_user clinician  Clinician123  Abena Clinician  c39a1f5e-8a11-4a3e-9f01-6ff8f7d10002
create_user clinicadmin ClinicAdmin123 Kojo Admin     c39a1f5e-8a11-4a3e-9f01-6ff8f7d10003
create_user support    Support12345  Sena  Support    c39a1f5e-8a11-4a3e-9f01-6ff8f7d10004

# Prescribers need a Provider record — the O3 drug-order workspace crashes on
# session.currentProvider.uuid without one (found in live workflow testing).
create_provider() { # username provider_identifier
  local person
  person=$(curl -s -u "$AUTH" \
    "$BASE/openmrs/ws/rest/v1/user?q=$1&v=custom:(username,systemId,person:(uuid))" \
    | python3 -c "
import json,sys
for r in json.load(sys.stdin)['results']:
    if (r.get('username') or r.get('systemId')) == '$1':
        print(r['person']['uuid']); break")
  [ -z "$person" ] && { echo "provider $2: user $1 not found"; return; }
  existing=$(curl -s -u "$AUTH" "$BASE/openmrs/ws/rest/v1/provider?v=custom:(person:(uuid))" \
    | python3 -c "
import json,sys
print(sum(1 for p in json.load(sys.stdin)['results'] if p['person']['uuid']=='$person'))")
  if [ "$existing" != "0" ]; then echo "provider $2: already exists"; return; fi
  code=$(curl -s -o /dev/null -w "%{http_code}" -u "$AUTH" \
    -X POST "$BASE/openmrs/ws/rest/v1/provider" \
    -H 'Content-Type: application/json' \
    -d "{\"person\": \"$person\", \"identifier\": \"$2\"}")
  echo "provider $2: HTTP $code"
}

create_provider admin     superuser
create_provider clinician clinician
