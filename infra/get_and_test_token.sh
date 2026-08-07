#!/bin/bash
TOKEN=$(curl -s -X POST http://keycloak:8081/realms/officemind/protocol/openid-connect/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=password" \
  -d "client_id=officemind-frontend" \
  -d "username=admin.user" \
  -d "password=Admin123!" | python3 -c "import sys, json; print(json.load(sys.stdin)['access_token'])")

echo "Token starts with: ${TOKEN:0:30}..."
echo ""
echo "--- Decoding issuer claim ---"
echo "$TOKEN" | cut -d. -f2 | base64 -D 2>/dev/null | python3 -m json.tool | grep iss

echo ""
echo "--- Calling protected endpoint ---"
curl -i http://localhost:8080/api/v1/users -H "Authorization: Bearer $TOKEN"
