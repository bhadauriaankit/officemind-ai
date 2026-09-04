-- V6: AI Configuration (Phase 5 polish)
-- Singleton settings row (fixed id) controlling the Ollama model,
-- temperature, and system prompt used by OllamaChatModelAdapter.

SET search_path TO officemind, public;

CREATE TABLE IF NOT EXISTS ai_settings (
    id             UUID PRIMARY KEY,
    model_name     VARCHAR(255) NOT NULL,
    temperature    DOUBLE PRECISION NOT NULL DEFAULT 0.7,
    system_prompt  TEXT,
    updated_at     TIMESTAMPTZ NOT NULL DEFAULT now()
);

INSERT INTO ai_settings (id, model_name, temperature, system_prompt, updated_at)
VALUES (
    '00000000-0000-0000-0000-000000000001',
    'llama3.2:1b',
    0.7,
    'You are OfficeMind AI, a private enterprise assistant. Be concise, professional, and helpful.',
    now()
)
ON CONFLICT (id) DO NOTHING;
