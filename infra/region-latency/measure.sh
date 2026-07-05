#!/usr/bin/env bash
# WP-004 — Region selection harness (master plan D6, ADR-001).
#
# Measures TCP connect / TLS handshake / TTFB from the machine it runs on to
# candidate cloud regions. Run it from Ghanaian vantage points: a clinic link
# when available, otherwise a Ghana-based VPS (and compare with RIPE Atlas
# probes in GH). Results from outside Ghana are useful only as a harness
# smoke test — label the vantage point honestly.
#
# Usage:
#   ./measure.sh [samples-per-endpoint] [vantage-label]
#   ./measure.sh 20 "accra-mtn-4g"
#
# Output: results-<vantage>-<utc timestamp>.csv + a summary table on stdout.

set -euo pipefail

SAMPLES="${1:-10}"
VANTAGE="${2:-unlabelled}"
ENDPOINTS_FILE="$(dirname "$0")/endpoints.txt"
OUT="results-${VANTAGE}-$(date -u +%Y%m%dT%H%M%SZ).csv"

command -v curl >/dev/null || { echo "curl required" >&2; exit 1; }

echo "vantage,region,endpoint,sample,dns_s,tcp_connect_s,tls_handshake_s,ttfb_s,http_code" > "$OUT"

while IFS='|' read -r region url; do
  [[ -z "$region" || "$region" == \#* ]] && continue
  echo "== $region  ($url)  x$SAMPLES" >&2
  for i in $(seq 1 "$SAMPLES"); do
    # -o /dev/null: body discarded; any HTTP status is fine — we time the wire.
    line=$(curl -s -o /dev/null --max-time 15 \
      -w "%{time_namelookup},%{time_connect},%{time_appconnect},%{time_starttransfer},%{http_code}" \
      "$url" || echo ",,,,000")
    echo "$VANTAGE,$region,$url,$i,$line" >> "$OUT"
    sleep 0.5
  done
done < "$ENDPOINTS_FILE"

echo
echo "Summary (median over $SAMPLES samples, seconds) — vantage: $VANTAGE"
printf "%-28s %10s %10s %10s\n" "region" "tcp_med" "tls_med" "ttfb_med"
awk -F, 'NR>1 && $9!="000" {tcp[$2]=tcp[$2]" "$6; tls[$2]=tls[$2]" "$7; ttfb[$2]=ttfb[$2]" "$8}
  END {
    for (r in tcp) {
      printf "%-28s %10s %10s %10s\n", r, med(tcp[r]), med(tls[r]), med(ttfb[r])
    }
  }
  function med(s,  a, n, i) {
    n = split(substr(s,2), a, " ")
    asort_simple(a, n)
    return (n % 2) ? a[(n+1)/2] : a[n/2]
  }
  function asort_simple(a, n,  i, j, t) {
    for (i = 1; i <= n; i++) for (j = i+1; j <= n; j++)
      if (a[j]+0 < a[i]+0) { t=a[i]; a[i]=a[j]; a[j]=t }
  }' "$OUT" | sort

echo
echo "Raw data: $OUT  (archive under docs/sources/ when used for ADR-001)"
