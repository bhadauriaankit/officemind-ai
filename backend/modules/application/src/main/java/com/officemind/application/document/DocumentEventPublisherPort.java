package com.officemind.application.document;

import com.officemind.domain.shared.EntityId;

/**
 * Port for publishing domain events related to documents.
 * Implemented in infrastructure by the Kafka adapter (Phase 8).
 */
public interface DocumentEventPublisherPort {

    /**
     * Publishes a "document uploaded" event so downstream consumers
     * (the async indexing pipeline) can pick it up without the upload
     * HTTP request waiting for embedding/Qdrant to finish.
     *
     * @param documentId  the ID of the just-stored document
     */
    void publishDocumentUploaded(EntityId documentId);
}
