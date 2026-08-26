package com.officemind.infrastructure.document;

import com.officemind.domain.document.Document;
import com.officemind.domain.document.DocumentStatus;
import com.officemind.domain.shared.EntityId;

final class DocumentMapper {

    private DocumentMapper() {
    }

    static Document toDomain(DocumentJpaEntity entity) {
        return Document.rehydrate(
                EntityId.of(entity.getId()),
                entity.getFileName(),
                entity.getContentType(),
                entity.getSizeBytes(),
                entity.getStorageKey(),
                entity.getVersion(),
                DocumentStatus.valueOf(entity.getStatus().name()),
                entity.getUploadedByUserId() != null ? entity.getUploadedByUserId().toString() : null,
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    static DocumentJpaEntity toJpa(Document document) {
        return new DocumentJpaEntity(
                document.getId().value(),
                document.getFileName(),
                document.getContentType(),
                document.getSizeBytes(),
                document.getStorageKey(),
                document.getVersion(),
                DocumentJpaEntity.StatusJpa.valueOf(document.getStatus().name()),
                document.getUploadedByUserId() != null ? java.util.UUID.fromString(document.getUploadedByUserId()) : null,
                document.getCreatedAt(),
                document.getUpdatedAt()
        );
    }
}
