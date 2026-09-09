package com.officemind.infrastructure.conversation;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "conversations", schema = "officemind")
public class ConversationJpaEntity {

    @Id
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "agent_id")
    private UUID agentId;

    private String title;

    /** Phase 10: rolling summary of older pruned messages. Null until first summarization. */
    @Column(columnDefinition = "TEXT")
    private String summary;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("sequenceNumber ASC")
    private List<MessageJpaEntity> messages = new ArrayList<>();

    protected ConversationJpaEntity() {
    }

    public ConversationJpaEntity(UUID id, UUID userId, UUID agentId, String title, String summary,
                                  Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.userId = userId;
        this.agentId = agentId;
        this.title = title;
        this.summary = summary;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public UUID getAgentId() { return agentId; }
    public String getTitle() { return title; }
    public String getSummary() { return summary; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public List<MessageJpaEntity> getMessages() { return messages; }
    public void setMessages(List<MessageJpaEntity> messages) { this.messages = messages; }
}
