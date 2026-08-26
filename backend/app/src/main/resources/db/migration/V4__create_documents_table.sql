-- V4: Knowledge Base document metadata (Phase 4)
-- Actual file bytes live in MinIO; this table tracks metadata, versioning,
-- and processing lifecycle. Chunking/embedding fields arrive in a later
-- migration once Phase 5's AI Engine gives us something to populate them with.

SET search_path TO officemind, public;

CREATE TABLE IF NOT EXISTS documents (
    id                  UUID PRIMARY KEY,
    file_name           VARCHAR(500) NOT NULL,
    content_type        VARCHAR(255),
    size_bytes          BIGINT NOT NULL,
    storage_key         VARCHAR(500) NOT NULL UNIQUE,
    version             INTEGER NOT NULL DEFAULT 1,
    status              VARCHAR(20) NOT NULL,
    uploaded_by_user_id UUID REFERENCES users(id),
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_documents_uploaded_by ON documents (uploaded_by_user_id);
CREATE INDEX IF NOT EXISTS idx_documents_status ON documents (status);
