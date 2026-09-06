package com.officemind.domain.documentchunk;

import com.officemind.domain.shared.AggregateRoot;
import com.officemind.domain.shared.EntityId;

import java.time.Instant;
import java.util.Objects;

/**
 * A single chunk of extracted text from a Document, sized for embedding.
 * The chunk's id doubles as its Qdrant point id (see VectorStorePort),
 * so the two stores stay trivially joinable without a separate mapping
 * table. Postgres is the source of truth for chunk text; Qdrant holds
 * the embedding plus a denormalized copy of the same text in its payload
 * purely so search results don't need a Postgres round-trip.
 */
public class DocumentChunk extends AggregateRoot {

    private final EntityId id;
    private final EntityId documentId;
    private final int chunkIndex;
    private final String content;
    private final Instant createdAt;

    private DocumentChunk(EntityId id, EntityId documentId, int chunkIndex,
                           String content, Instant createdAt) {
        this.id = Objects.requireNonNull(id);
        this.documentId = Objects.requireNonNull(documentId);
        this.chunkIndex = chunkIndex;
        this.content = Objects.requireNonNull(content, "content is required");
        this.createdAt = createdAt;
    }

    public static DocumentChunk create(EntityId documentId, int chunkIndex, String content) {
        return new DocumentChunk(EntityId.generate(), documentId, chunkIndex, content, Instant.now());
    }

    public static DocumentChunk rehydrate(EntityId id, EntityId documentId, int chunkIndex,
                                           String content, Instant createdAt) {
        return new DocumentChunk(id, documentId, chunkIndex, content, createdAt);
    }

    public EntityId getId() { return id; }
    public EntityId getDocumentId() { return documentId; }
    public int getChunkIndex() { return chunkIndex; }
    public String getContent() { return content; }
    public Instant getCreatedAt() { return createdAt; }
}
