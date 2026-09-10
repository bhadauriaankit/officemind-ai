package com.officemind.application.document;

import com.officemind.domain.document.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.UUID;

@Service
public class UploadDocumentUseCase {

    private static final Logger log = LoggerFactory.getLogger(UploadDocumentUseCase.class);

    private final DocumentRepositoryPort documentRepository;
    private final FileStoragePort fileStoragePort;
    private final DocumentEventPublisherPort eventPublisher;

    public UploadDocumentUseCase(DocumentRepositoryPort documentRepository,
                                  FileStoragePort fileStoragePort,
                                  DocumentEventPublisherPort eventPublisher) {
        this.documentRepository = documentRepository;
        this.fileStoragePort = fileStoragePort;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Stores the file in MinIO, persists metadata with status=UPLOADED, then
     * publishes a Kafka event so the indexing pipeline picks it up asynchronously.
     * The HTTP response returns immediately -- upload latency is no longer coupled
     * to embedding/Qdrant latency (Phase 8).
     */
    public Document execute(String fileName, String contentType, long sizeBytes,
                             InputStream content, String uploadedByUserId) {
        String storageKey = "documents/%s/%s".formatted(UUID.randomUUID(), fileName);

        fileStoragePort.store(storageKey, content, sizeBytes, contentType);

        Document document = Document.upload(fileName, contentType, sizeBytes, storageKey, uploadedByUserId);
        document = documentRepository.save(document);

        // Phase 8: async indexing via Kafka.  The consumer (KafkaDocumentEventListener)
        // will call IndexDocumentUseCase when it receives this event, so the upload
        // HTTP response returns as soon as the file is persisted -- not after
        // potentially-slow embedding + Qdrant upsert completes.
        eventPublisher.publishDocumentUploaded(document.getId());
        log.info("Document {} uploaded, indexing event published", document.getId().value());

        return document;
    }
}
