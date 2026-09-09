-- V10: Conversation summary for context-window pruning (Phase 10)
-- When a conversation exceeds the summarization threshold, older messages
-- are deleted and their content is condensed into this column. The summary
-- is injected as a system message on every subsequent LLM call so the model
-- retains context without the full message history growing unboundedly.

SET search_path TO officemind, public;

ALTER TABLE conversations ADD COLUMN IF NOT EXISTS summary TEXT;
