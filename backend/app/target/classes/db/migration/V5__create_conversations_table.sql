-- V5: AI Engine conversation history (Phase 5)

SET search_path TO officemind, public;

CREATE TABLE IF NOT EXISTS conversations (
    id           UUID PRIMARY KEY,
    user_id      UUID NOT NULL REFERENCES users(id),
    title        VARCHAR(255),
    created_at   TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at   TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_conversations_user ON conversations (user_id);

CREATE TABLE IF NOT EXISTS conversation_messages (
    id               UUID PRIMARY KEY,
    conversation_id  UUID NOT NULL REFERENCES conversations(id) ON DELETE CASCADE,
    role             VARCHAR(20) NOT NULL,
    content          TEXT NOT NULL,
    sent_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    sequence_number  INTEGER NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_conversation_messages_conversation
    ON conversation_messages (conversation_id, sequence_number);
