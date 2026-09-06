package com.officemind.application.documentchunk;

import com.officemind.domain.shared.EntityId;

import java.util.List;

public interface VectorStorePort {

    record IndexedChunk(EntityId chunkId, float[] vector, EntityId documentId,
                         int chunkIndex, String content) {
    }

    record SearchHit(EntityId chunkId, EntityId documentId, int chunkIndex,
                      String content, float score) {
    }

    /** Idempotently ensures the backing collection exists with the right vector size. */
    void ensureCollectionExists();

    void upsert(List<IndexedChunk> chunks);

    void deleteByDocumentId(EntityId documentId);

    List<SearchHit> search(float[] queryVector, int limit);
}
