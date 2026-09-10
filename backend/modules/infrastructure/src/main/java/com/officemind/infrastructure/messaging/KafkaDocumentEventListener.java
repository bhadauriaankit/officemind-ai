package com.officemind.infrastructure.messaging;

import com.officemind.application.documentchunk.IndexDocumentUseCase;
import com.officemind.application.document.DocumentRepositoryPort;
import com.officemind.application.notification.CreateNotificationUseCase;
import com.officemind.domain.document.Document;
import com.officemind.domain.notification.NotificationType;
import com.officemind.domain.shared.EntityId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

/**
 * Consumes document.uploaded events and drives the async indexing pipeline.
 * On completion publishes a document.indexed event and creates a user notification
 * so the uploader sees feedback in the UI (Phase 11).
 */
@Component
public class KafkaDocumentEventListener {

    private static final Logger log = LoggerFactory.getLogger(KafkaDocumentEventListener.class);

    private final IndexDocumentUseCase indexDocumentUseCase;
    private final DocumentRepositoryPort documentRepository;
    private final CreateNotificationUseCase createNotificationUseCase;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final String documentIndexedTopic;

    public KafkaDocumentEventListener(
            IndexDocumentUseCase indexDocumentUseCase,
            DocumentRepositoryPort documentRepository,
            CreateNotificationUseCase createNotificationUseCase,
            KafkaTemplate<String, String> kafkaTemplate,
            @Value("${officemind.kafka.topics.document-indexed:document.indexed}") String documentIndexedTopic) {
        this.indexDocumentUseCase = indexDocumentUseCase;
        this.documentRepository = documentRepository;
        this.createNotificationUseCase = createNotificationUseCase;
        this.kafkaTemplate = kafkaTemplate;
        this.documentIndexedTopic = documentIndexedTopic;
    }

    @KafkaListener(
            topics = "${officemind.kafka.topics.document-uploaded:document.uploaded}",
            groupId = "officemind-indexing-pipeline",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void onDocumentUploaded(String documentIdStr) {
        log.info("Received document.uploaded event: documentId={}", documentIdStr);
        EntityId documentId = EntityId.of(UUID.fromString(documentIdStr));

        // Resolve the uploader before indexing (status may change during indexing)
        Optional<Document> docOpt = documentRepository.findById(documentId);
        String uploaderUserId = docOpt.map(Document::getUploadedByUserId).orElse(null);
        String fileName = docOpt.map(Document::getFileName).orElse(documentIdStr);

        try {
            indexDocumentUseCase.execute(documentId);
            kafkaTemplate.send(documentIndexedTopic, documentIdStr, documentIdStr + ":SUCCESS");

            // Phase 11: notify the uploader that indexing succeeded
            if (uploaderUserId != null) {
                createNotificationUseCase.execute(
                        uploaderUserId,
                        NotificationType.DOCUMENT_INDEXED,
                        "Document ready",
                        "\"" + fileName + "\" has been indexed and is now searchable."
                );
            }
            log.info("Indexing complete for documentId={}", documentIdStr);
        } catch (Exception e) {
            log.error("Indexing failed for documentId={}", documentIdStr, e);
            kafkaTemplate.send(documentIndexedTopic, documentIdStr, documentIdStr + ":FAILED");

            if (uploaderUserId != null) {
                createNotificationUseCase.execute(
                        uploaderUserId,
                        NotificationType.DOCUMENT_INDEX_FAILED,
                        "Document indexing failed",
                        "\"" + fileName + "\" could not be indexed. Please try re-uploading."
                );
            }
        }
    }
}
