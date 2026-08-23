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
