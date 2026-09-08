package com.officemind.application.conversation;

import com.officemind.domain.conversation.Message;

import java.util.List;

public interface ChatModelPort {

    /**
     * Sends the full message history to the LLM and returns its reply text.
     *
     * @param retrievedContext  optional RAG context (relevant document
     *                          chunks) to ground the reply in; pass null
     *                          or blank when there's nothing relevant.
     * @param systemPromptOverride  optional per-conversation system prompt
     *                          (Phase 7: a selected persona Agent's
     *                          prompt) that takes priority over the
     *                          global AiSettings prompt when present;
     *                          pass null to fall back to the global one.
     */
    String generateReply(List<Message> conversationHistory, String retrievedContext, String systemPromptOverride);
}
