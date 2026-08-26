package com.officemind.api.document;

import com.officemind.domain.document.Document;
import com.officemind.domain.document.DocumentStatus;

import java.time.Instant;
import java.util.UUID;

public record DocumentResponse(
        UUID id,
        String fileName,
        String contentType,
        long sizeBytes,
        int version,
        DocumentStatus status,
        Instant createdAt,
        Instant updatedAt
) {
    public static DocumentResponse from(Document document) {
        return new DocumentResponse(
                document.getId().value(),
                document.getFileName(),
                document.getContentType(),
                document.getSizeBytes(),
                document.getVersion(),
                document.getStatus(),
                document.getCreatedAt(),
                document.getUpdatedAt()
        );
    }
}
