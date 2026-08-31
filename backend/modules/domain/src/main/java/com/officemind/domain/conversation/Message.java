package com.officemind.domain.conversation;

import java.time.Instant;
import java.util.Objects;

public class Message {

    private final MessageRole role;
    private final String content;
    private final Instant sentAt;

    public Message(MessageRole role, String content, Instant sentAt) {
        this.role = Objects.requireNonNull(role);
        this.content = Objects.requireNonNull(content, "content is required");
        this.sentAt = Objects.requireNonNull(sentAt);
    }

    public static Message userMessage(String content) {
        return new Message(MessageRole.USER, content, Instant.now());
    }

    public static Message assistantMessage(String content) {
        return new Message(MessageRole.ASSISTANT, content, Instant.now());
    }

    public MessageRole getRole() { return role; }
    public String getContent() { return content; }
    public Instant getSentAt() { return sentAt; }
}
