-- V3: Department Management (Phase 3)

SET search_path TO officemind, public;

CREATE TABLE IF NOT EXISTS departments (
    id            UUID PRIMARY KEY,
    name          VARCHAR(255) NOT NULL,
    description   TEXT,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at    TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_departments_name ON departments (name);

-- Link users to a department (nullable — not every user needs one assigned)
ALTER TABLE users ADD COLUMN IF NOT EXISTS department_id UUID REFERENCES departments(id);
