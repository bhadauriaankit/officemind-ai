#!/bin/bash
echo "===================================="
echo "1. Container status"
echo "===================================="
docker compose ps

echo ""
echo "===================================="
echo "2. Backend health endpoint"
echo "===================================="
curl -s http://localhost:8080/api/v1/system/health | python3 -m json.tool

echo ""
echo "===================================="
echo "3. Keycloak realm reachable"
echo "===================================="
curl -s -o /dev/null -w "HTTP Status: %{http_code}\n" http://localhost:8081/realms/officemind/.well-known/openid-configuration

echo ""
echo "===================================="
echo "4. Protected endpoint correctly requires auth"
echo "===================================="
curl -s -o /dev/null -w "HTTP Status: %{http_code} (expected: 401)\n" http://localhost:8080/api/v1/users

echo ""
echo "===================================="
echo "5. Frontend reachable"
echo "===================================="
curl -s -o /dev/null -w "HTTP Status: %{http_code} (expected: 200)\n" http://localhost:3000

echo ""
echo "===================================="
echo "Done."
echo "===================================="
