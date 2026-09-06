package com.officemind.infrastructure.documentchunk;

import com.officemind.domain.documentchunk.DocumentChunk;
import com.officemind.domain.shared.EntityId;

final class DocumentChunkMapper {

    private DocumentChunkMapper() {
    }

    static DocumentChunk toDomain(DocumentChunkJpaEntity entity) {
        return DocumentChunk.rehydrate(
                EntityId.of(entity.getId()),
                EntityId.of(entity.getDocumentId()),
                entity.getChunkIndex(),
                entity.getContent(),
                entity.getCreatedAt()
        );
    }

    static DocumentChunkJpaEntity toJpa(DocumentChunk chunk) {
        return new DocumentChunkJpaEntity(
                chunk.getId().value(),
                chunk.getDocumentId().value(),
                chunk.getChunkIndex(),
                chunk.getContent(),
                chunk.getCreatedAt()
        );
    }
}
