#!/bin/bash
docker exec officemind-backend wget -qO- \
  --post-data="grant_type=password&client_id=officemind-frontend&username=admin.user&password=Admin123!" \
  --header="Content-Type: application/x-www-form-urlencoded" \
  http://keycloak:8080/realms/officemind/protocol/openid-connect/token > /tmp/token_response.json

cat /tmp/token_response.json | python3 -m json.tool

TOKEN=$(cat /tmp/token_response.json | python3 -c "import sys, json; print(json.load(sys.stdin)['access_token'])")
echo "$TOKEN" > /tmp/officemind_token.txt
echo ""
echo "Token starts with: ${TOKEN:0:30}..."
echo ""
echo "--- Issuer claim ---"
echo "$TOKEN" | cut -d. -f2 | base64 -D 2>/dev/null | python3 -m json.tool | grep iss
