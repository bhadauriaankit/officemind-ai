-- V7: RAG (Phase 6) — document chunk metadata.
-- The embedding vector for each chunk lives in Qdrant, keyed by this
-- table's id (used as the Qdrant point id). This table is Postgres's
-- source of truth for chunk text and lets us re-derive/re-index without
-- re-parsing the original file, and lets document deletion cascade
-- cleanly to its chunks.

SET search_path TO officemind, public;

CREATE TABLE IF NOT EXISTS document_chunks (
    id           UUID PRIMARY KEY,
    document_id  UUID NOT NULL REFERENCES documents(id) ON DELETE CASCADE,
    chunk_index  INT NOT NULL,
    content      TEXT NOT NULL,
    created_at   TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (document_id, chunk_index)
);

CREATE INDEX IF NOT EXISTS idx_document_chunks_document_id ON document_chunks (document_id);
