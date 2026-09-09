package com.officemind.application.conversation;

import com.officemind.domain.conversation.Message;

import java.util.List;

public interface ChatModelPort {

    /**
     * Sends the message history to the LLM and returns its reply text.
     *
     * @param conversationHistory  the tail of recent messages (may be a
     *                             pruned subset after Phase 10 condensation)
     * @param retrievedContext     optional RAG context (relevant document
     *                             chunks) to ground the reply in; null/blank
     *                             when there is nothing relevant
     * @param systemPromptOverride optional per-conversation system prompt
     *                             (Phase 7: selected persona Agent's prompt)
     *                             that takes priority over the global
     *                             AiSettings prompt; null falls back to global
     * @param conversationSummary  optional rolling summary of older messages
     *                             that were pruned (Phase 10); injected as a
     *                             system message so the model retains earlier
     *                             context without seeing the full history;
     *                             null when no summarization has occurred yet
     */
    String generateReply(List<Message> conversationHistory,
                         String retrievedContext,
                         String systemPromptOverride,
                         String conversationSummary);
}
