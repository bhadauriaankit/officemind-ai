package com.officemind.infrastructure.document;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "documents", schema = "officemind")
public class DocumentJpaEntity {

    @Id
    private UUID id;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "content_type")
    private String contentType;

    @Column(name = "size_bytes", nullable = false)
    private long sizeBytes;

    @Column(name = "storage_key", nullable = false, unique = true)
    private String storageKey;

    @Column(nullable = false)
    private int version;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusJpa status;

    @Column(name = "uploaded_by_user_id")
    private UUID uploadedByUserId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected DocumentJpaEntity() {
    }

    public DocumentJpaEntity(UUID id, String fileName, String contentType, long sizeBytes,
                              String storageKey, int version, StatusJpa status, UUID uploadedByUserId,
                              Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.fileName = fileName;
        this.contentType = contentType;
        this.sizeBytes = sizeBytes;
        this.storageKey = storageKey;
        this.version = version;
        this.status = status;
        this.uploadedByUserId = uploadedByUserId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public enum StatusJpa { UPLOADED, PROCESSING, READY, FAILED }

    public UUID getId() { return id; }
    public String getFileName() { return fileName; }
    public String getContentType() { return contentType; }
    public long getSizeBytes() { return sizeBytes; }
    public String getStorageKey() { return storageKey; }
    public int getVersion() { return version; }
    public StatusJpa getStatus() { return status; }
    public UUID getUploadedByUserId() { return uploadedByUserId; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
