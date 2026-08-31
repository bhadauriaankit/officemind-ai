package com.officemind.infrastructure.conversation;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "conversation_messages", schema = "officemind")
public class MessageJpaEntity {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id", nullable = false)
    private ConversationJpaEntity conversation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoleJpa role;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "sent_at", nullable = false)
    private Instant sentAt;

    @Column(name = "sequence_number", nullable = false)
    private int sequenceNumber;

    protected MessageJpaEntity() {
    }

    public MessageJpaEntity(UUID id, RoleJpa role, String content, Instant sentAt, int sequenceNumber) {
        this.id = id;
        this.role = role;
        this.content = content;
        this.sentAt = sentAt;
        this.sequenceNumber = sequenceNumber;
    }

    public enum RoleJpa { USER, ASSISTANT }

    public UUID getId() { return id; }
    public ConversationJpaEntity getConversation() { return conversation; }
    public void setConversation(ConversationJpaEntity conversation) { this.conversation = conversation; }
    public RoleJpa getRole() { return role; }
    public String getContent() { return content; }
    public Instant getSentAt() { return sentAt; }
    public int getSequenceNumber() { return sequenceNumber; }
}
