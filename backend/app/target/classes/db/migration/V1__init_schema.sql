-- V1: Baseline schema bootstrap for OfficeMind AI.
-- Intentionally minimal: this is Phase 1 (project initialization/infra).
-- Auth tables arrive in V2 (Phase 2), admin/org tables in V3 (Phase 3), etc.
-- Keeping migrations additive and phase-scoped avoids destructive rewrites later.

CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE SCHEMA IF NOT EXISTS officemind;

SET search_path TO officemind, public;

-- Generic audit columns are reused via inheritance-by-convention across
-- future tables (created_at/updated_at/created_by/updated_by), enforced by
-- a shared Hibernate @MappedSuperclass in the domain layer from Phase 2 on.

CREATE TABLE IF NOT EXISTS schema_bootstrap_marker (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    component     VARCHAR(100) NOT NULL,
    initialized_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

INSERT INTO schema_bootstrap_marker (component) VALUES ('officemind-ai-phase1');
