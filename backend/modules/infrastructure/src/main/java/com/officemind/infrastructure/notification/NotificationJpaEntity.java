package com.officemind.infrastructure.notification;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "notifications", schema = "officemind")
public class NotificationJpaEntity {

    @Id
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeJpa type;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String body;

    @Column(name = "is_read", nullable = false)
    private boolean read;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected NotificationJpaEntity() {}

    public NotificationJpaEntity(UUID id, UUID userId, TypeJpa type, String title,
                                  String body, boolean read, Instant createdAt) {
        this.id = id; this.userId = userId; this.type = type; this.title = title;
        this.body = body; this.read = read; this.createdAt = createdAt;
    }

    public enum TypeJpa { DOCUMENT_INDEXED, DOCUMENT_INDEX_FAILED }

    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public TypeJpa getType() { return type; }
    public String getTitle() { return title; }
    public String getBody() { return body; }
    public boolean isRead() { return read; }
    public Instant getCreatedAt() { return createdAt; }
    public void setRead(boolean read) { this.read = read; }
}
