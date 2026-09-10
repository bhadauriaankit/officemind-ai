package com.officemind.infrastructure.messaging;

import com.officemind.application.document.DocumentEventPublisherPort;
import com.officemind.domain.shared.EntityId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Implements DocumentEventPublisherPort by publishing to Kafka.
 * Uses String keys and plain UUID string values -- simple and avoids
 * a schema-registry dependency for this project's scale.
 */
@Component
public class KafkaDocumentEventAdapter implements DocumentEventPublisherPort {

    private static final Logger log = LoggerFactory.getLogger(KafkaDocumentEventAdapter.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final String documentUploadedTopic;

    public KafkaDocumentEventAdapter(
            KafkaTemplate<String, String> kafkaTemplate,
            @Value("${officemind.kafka.topics.document-uploaded:document.uploaded}") String documentUploadedTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.documentUploadedTopic = documentUploadedTopic;
    }

    @Override
    public void publishDocumentUploaded(EntityId documentId) {
        String id = documentId.value().toString();
        kafkaTemplate.send(documentUploadedTopic, id, id)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish document.uploaded for id={}", id, ex);
                    } else {
                        log.debug("Published document.uploaded: id={} partition={} offset={}",
                                id,
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset());
                    }
                });
    }
}
