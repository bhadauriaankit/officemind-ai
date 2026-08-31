package com.officemind.application.conversation;

import com.officemind.domain.conversation.Message;

import java.util.List;

public interface ChatModelPort {

    /** Sends the full message history to the LLM and returns its reply text. */
    String generateReply(List<Message> conversationHistory);
}
