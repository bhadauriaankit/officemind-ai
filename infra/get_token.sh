#!/bin/bash
curl -s -X POST http://localhost:8081/realms/officemind/protocol/openid-connect/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=password" \
  -d "client_id=officemind-frontend" \
  -d "username=admin.user" \
  -d "password=Admin123!" | python3 -m json.tool

