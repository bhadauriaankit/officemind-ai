-- V11: User notifications (Phase 11)
-- Stores system-generated notifications (e.g. document indexed/failed).
-- Consumers poll GET /api/v1/notifications?unreadOnly=true; marking read
-- via PATCH /api/v1/notifications/{id}/read.

SET search_path TO officemind, public;

CREATE TABLE IF NOT EXISTS notifications (
    id          UUID PRIMARY KEY,
    user_id     UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    type        VARCHAR(50) NOT NULL,
    title       VARCHAR(255) NOT NULL,
    body        TEXT,
    is_read     BOOLEAN NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_notifications_user_unread
    ON notifications (user_id, is_read, created_at DESC);
