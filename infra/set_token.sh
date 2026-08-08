#!/bin/bash
TOKEN=$(curl -s -X POST http://keycloak:8081/realms/officemind/protocol/openid-connect/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=password" \
  -d "client_id=officemind-frontend" \
  -d "username=admin.user" \
  -d "password=Admin123!" | python3 -c "import sys, json; print(json.load(sys.stdin)['access_token'])")

echo "$TOKEN" > /tmp/officemind_token.txt
echo "Token saved. Starts with: ${TOKEN:0:30}..."
