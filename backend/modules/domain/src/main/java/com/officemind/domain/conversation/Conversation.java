package com.officemind.domain.conversation;

import com.officemind.domain.shared.AggregateRoot;
import com.officemind.domain.shared.EntityId;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A chat session between one user and the assistant. Holds message history
 * in-memory as a simple list; large-scale summarization/pruning is a later
 * concern once conversations get long (Phase 10: Conversation Engine).
 */
public class Conversation extends AggregateRoot {

    private final EntityId id;
    private final String userId;
    private String title;
    private final List<Message> messages;
    private final Instant createdAt;
    private Instant updatedAt;

    private Conversation(EntityId id, String userId, String title, List<Message> messages,
                          Instant createdAt, Instant updatedAt) {
        this.id = Objects.requireNonNull(id);
        this.userId = Objects.requireNonNull(userId, "userId is required");
        this.title = title;
        this.messages = new ArrayList<>(messages);
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Conversation start(String userId, String firstUserMessage) {
        Instant now = Instant.now();
        Conversation conversation = new Conversation(
                EntityId.generate(), userId, deriveTitleFrom(firstUserMessage),
                new ArrayList<>(), now, now
        );
        conversation.appendMessage(Message.userMessage(firstUserMessage));
        return conversation;
    }

    public static Conversation rehydrate(EntityId id, String userId, String title,
                                          List<Message> messages, Instant createdAt, Instant updatedAt) {
        return new Conversation(id, userId, title, messages, createdAt, updatedAt);
    }

    public void appendMessage(Message message) {
        this.messages.add(message);
        this.updatedAt = Instant.now();
    }

    private static String deriveTitleFrom(String firstMessage) {
        String trimmed = firstMessage.strip();
        return trimmed.length() > 60 ? trimmed.substring(0, 60) + "…" : trimmed;
    }

    public EntityId getId() { return id; }
    public String getUserId() { return userId; }
    public String getTitle() { return title; }
    public List<Message> getMessages() { return List.copyOf(messages); }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
