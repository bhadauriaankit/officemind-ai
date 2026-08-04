-- Runs once on first container start (docker-entrypoint-initdb.d convention).
-- Flyway (V1__init_schema.sql) also guards this, but we install it here too
-- so manual psql sessions and other tooling can rely on it immediately.
CREATE EXTENSION IF NOT EXISTS pgcrypto;
