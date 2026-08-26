package com.officemind.domain.document;

import com.officemind.domain.shared.AggregateRoot;
import com.officemind.domain.shared.EntityId;

import java.time.Instant;
import java.util.Objects;

/**
 * Metadata for an uploaded file. The actual bytes live in MinIO under
 * {@code storageKey}; this aggregate tracks lifecycle (status), simple
 * versioning (each re-upload of the same logical document increments
 * version and gets a new storageKey, old versions are retained), and
 * whatever downstream processing (chunking/embedding, added in Phase 5/6)
 * has been done.
 */
public class Document extends AggregateRoot {

    private final EntityId id;
    private String fileName;
    private String contentType;
    private long sizeBytes;
    private String storageKey;
    private int version;
    private DocumentStatus status;
    private String uploadedByUserId;
    private final Instant createdAt;
    private Instant updatedAt;

    private Document(EntityId id, String fileName, String contentType, long sizeBytes,
                      String storageKey, int version, DocumentStatus status,
                      String uploadedByUserId, Instant createdAt, Instant updatedAt) {
        this.id = Objects.requireNonNull(id);
        this.fileName = Objects.requireNonNull(fileName, "fileName is required");
        this.contentType = contentType;
        this.sizeBytes = sizeBytes;
        this.storageKey = Objects.requireNonNull(storageKey, "storageKey is required");
        this.version = version;
        this.status = status;
        this.uploadedByUserId = uploadedByUserId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Document upload(String fileName, String contentType, long sizeBytes,
                                   String storageKey, String uploadedByUserId) {
        Instant now = Instant.now();
        return new Document(
                EntityId.generate(), fileName, contentType, sizeBytes, storageKey,
                1, DocumentStatus.UPLOADED, uploadedByUserId, now, now
        );
    }

    public static Document rehydrate(EntityId id, String fileName, String contentType, long sizeBytes,
                                      String storageKey, int version, DocumentStatus status,
                                      String uploadedByUserId, Instant createdAt, Instant updatedAt) {
        return new Document(id, fileName, contentType, sizeBytes, storageKey, version, status,
                uploadedByUserId, createdAt, updatedAt);
    }

    public void markProcessing() {
        this.status = DocumentStatus.PROCESSING;
        this.updatedAt = Instant.now();
    }

    public void markReady() {
        this.status = DocumentStatus.READY;
        this.updatedAt = Instant.now();
    }

    public void markFailed() {
        this.status = DocumentStatus.FAILED;
        this.updatedAt = Instant.now();
    }

    public EntityId getId() { return id; }
    public String getFileName() { return fileName; }
    public String getContentType() { return contentType; }
    public long getSizeBytes() { return sizeBytes; }
    public String getStorageKey() { return storageKey; }
    public int getVersion() { return version; }
    public DocumentStatus getStatus() { return status; }
    public String getUploadedByUserId() { return uploadedByUserId; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
