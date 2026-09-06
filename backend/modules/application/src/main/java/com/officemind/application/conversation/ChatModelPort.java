package com.officemind.application.conversation;

import com.officemind.domain.conversation.Message;

import java.util.List;

public interface ChatModelPort {

    /**
     * Sends the full message history to the LLM and returns its reply text.
     *
     * @param retrievedContext optional RAG context (relevant document
     *                         chunks) to ground the reply in; pass null
     *                         or blank when there's nothing relevant.
     */
    String generateReply(List<Message> conversationHistory, String retrievedContext);
}
