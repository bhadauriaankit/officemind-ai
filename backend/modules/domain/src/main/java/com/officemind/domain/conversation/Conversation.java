package com.officemind.domain.conversation;

import com.officemind.domain.shared.AggregateRoot;
import com.officemind.domain.shared.EntityId;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * A chat session between one user and the assistant. Holds message history
 * in-memory as a simple list.
 *
 * Phase 10 (Conversation Engine): once message count exceeds a threshold,
 * older messages are pruned from this list (and from the DB on next save)
 * and their content is condensed into {@code summary}. The summary is
 * injected as a system message on every subsequent LLM call, keeping the
 * model informed without growing the context window unboundedly.
 *
 * agentId (Phase 7): which persona agent this conversation is using, if
 * any. Chosen once at start and immutable after -- but the agent's
 * content (its system prompt) is still resolved live on every message,
 * same pattern as AiSettings, so editing an agent takes effect on
 * existing conversations immediately.
 */
public class Conversation extends AggregateRoot {

    private final EntityId id;
    private final String userId;
    private final EntityId agentId;
    private String title;
    private final List<Message> messages;
    private String summary;
    private final Instant createdAt;
    private Instant updatedAt;

    private Conversation(EntityId id, String userId, EntityId agentId, String title,
                         List<Message> messages, String summary,
                         Instant createdAt, Instant updatedAt) {
        this.id = Objects.requireNonNull(id);
        this.userId = Objects.requireNonNull(userId, "userId is required");
        this.agentId = agentId;
        this.title = title;
        this.messages = new ArrayList<>(messages);
        this.summary = summary;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Conversation start(String userId, String firstUserMessage, EntityId agentId) {
        Instant now = Instant.now();
        Conversation conversation = new Conversation(
                EntityId.generate(), userId, agentId, deriveTitleFrom(firstUserMessage),
                new ArrayList<>(), null, now, now
        );
        conversation.appendMessage(Message.userMessage(firstUserMessage));
        return conversation;
    }

    public static Conversation rehydrate(EntityId id, String userId, EntityId agentId, String title,
                                          List<Message> messages, String summary,
                                          Instant createdAt, Instant updatedAt) {
        return new Conversation(id, userId, agentId, title, messages, summary, createdAt, updatedAt);
    }

    public void appendMessage(Message message) {
        this.messages.add(message);
        this.updatedAt = Instant.now();
    }

    /**
     * Removes all but the {@code keepCount} most-recent messages from this
     * conversation's in-memory list and returns the removed (older) messages
     * so the caller can summarize them.
     *
     * This does NOT set the summary -- the caller is responsible for calling
     * {@link #setSummary(String)} with the result of summarising the returned
     * messages (merged with any pre-existing summary, if present).
     *
     * Returns an empty list when message count is already <= keepCount.
     */
    public List<Message> pruneMessagesOlderThan(int keepCount) {
        if (messages.size() <= keepCount) {
            return List.of();
        }
        int pruneUpTo = messages.size() - keepCount;
        List<Message> pruned = new ArrayList<>(messages.subList(0, pruneUpTo));
        messages.subList(0, pruneUpTo).clear();
        this.updatedAt = Instant.now();
        return pruned;
    }

    /** Stores the condensed summary produced after pruning older messages. */
    public void setSummary(String summary) {
        this.summary = summary;
        this.updatedAt = Instant.now();
    }

    private static String deriveTitleFrom(String firstMessage) {
        String trimmed = firstMessage.strip();
        return trimmed.length() > 60 ? trimmed.substring(0, 60) + "..." : trimmed;
    }

    public EntityId getId() { return id; }
    public String getUserId() { return userId; }
    public Optional<EntityId> getAgentId() { return Optional.ofNullable(agentId); }
    public String getTitle() { return title; }
    public List<Message> getMessages() { return List.copyOf(messages); }
    public Optional<String> getSummary() { return Optional.ofNullable(summary); }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
