package com.officemind.api.conversation;

import com.officemind.domain.conversation.Conversation;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ConversationResponse(
        UUID id,
        String title,
        List<MessageResponse> messages,
        Instant createdAt,
        Instant updatedAt
) {
    public static ConversationResponse from(Conversation conversation) {
        return new ConversationResponse(
                conversation.getId().value(),
                conversation.getTitle(),
                conversation.getMessages().stream().map(MessageResponse::from).toList(),
                conversation.getCreatedAt(),
                conversation.getUpdatedAt()
        );
    }
}
