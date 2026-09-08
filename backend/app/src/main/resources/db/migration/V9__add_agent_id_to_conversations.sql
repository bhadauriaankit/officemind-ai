-- V9: link conversations to an optional persona agent (Phase 7 v1).
-- ON DELETE SET NULL: deleting an agent shouldn't break old conversations
-- that used it -- they just fall back to the global AiSettings prompt.

SET search_path TO officemind, public;

ALTER TABLE conversations
    ADD COLUMN IF NOT EXISTS agent_id UUID REFERENCES agents(id) ON DELETE SET NULL;
