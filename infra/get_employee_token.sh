#!/bin/bash
docker exec officemind-backend wget -qO- \
  --post-data="grant_type=password&client_id=officemind-frontend&username=employee.user&password=Employee123!" \
  --header="Content-Type: application/x-www-form-urlencoded" \
  http://keycloak:8080/realms/officemind/protocol/openid-connect/token \
  > /tmp/employee_token_response.json 2>/tmp/employee_token_error.txt

echo "--- STDOUT (successful response body, if any) ---"
cat /tmp/employee_token_response.json
echo ""
echo "--- STDERR (wget's own error output) ---"
cat /tmp/employee_token_error.txt
echo ""
echo "--- Parsed (if valid JSON) ---"
cat /tmp/employee_token_response.json | python3 -m json.tool 2>/dev/null || echo "(not valid JSON)"

TOKEN=$(cat /tmp/employee_token_response.json | python3 -c "import sys, json; d=json.load(sys.stdin); print(d.get('access_token',''))" 2>/dev/null)
echo "$TOKEN" > /tmp/employee_token.txt
echo ""
echo "Employee token starts with: ${TOKEN:0:30}..."
