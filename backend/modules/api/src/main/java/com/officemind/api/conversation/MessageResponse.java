package com.officemind.api.conversation;

import com.officemind.domain.conversation.Message;
import com.officemind.domain.conversation.MessageRole;

import java.time.Instant;

public record MessageResponse(MessageRole role, String content, Instant sentAt) {
    public static MessageResponse from(Message message) {
        return new MessageResponse(message.getRole(), message.getContent(), message.getSentAt());
    }
}
