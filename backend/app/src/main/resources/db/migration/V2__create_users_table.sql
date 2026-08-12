-- V2: User Management (Phase 2)
-- Local profile mirroring Keycloak identity, plus RBAC role assignments.

SET search_path TO officemind, public;

CREATE TABLE IF NOT EXISTS users (
    id                    UUID PRIMARY KEY,
    keycloak_subject_id   VARCHAR(255) NOT NULL,
    email                 VARCHAR(255) NOT NULL,
    display_name          VARCHAR(255),
    department            VARCHAR(255),
    status                VARCHAR(20) NOT NULL,
    created_at            TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at            TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_users_keycloak_subject ON users (keycloak_subject_id);
CREATE UNIQUE INDEX IF NOT EXISTS idx_users_email ON users (email);

CREATE TABLE IF NOT EXISTS user_roles (
    user_id  UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role     VARCHAR(50) NOT NULL,
    PRIMARY KEY (user_id, role)
);
