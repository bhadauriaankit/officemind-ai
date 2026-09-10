package com.officemind.infrastructure.messaging;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * Declares the Kafka topics this application owns.
 * Spring Boot's KafkaAdmin auto-creates any topics it finds as beans,
 * so declaring them here is sufficient -- no manual kafka-topics.sh needed.
 *
 * Phase 8: document.uploaded -- triggers async RAG indexing pipeline.
 * Phase 11: document.indexed -- signals indexing complete/failed for notifications.
 */
@Configuration
public class KafkaTopicConfig {

    @Value("${officemind.kafka.topics.document-uploaded:document.uploaded}")
    private String documentUploadedTopic;

    @Value("${officemind.kafka.topics.document-indexed:document.indexed}")
    private String documentIndexedTopic;

    @Bean
    public NewTopic documentUploadedTopic() {
        return TopicBuilder.name(documentUploadedTopic)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic documentIndexedTopic() {
        return TopicBuilder.name(documentIndexedTopic)
                .partitions(1)
                .replicas(1)
                .build();
    }
}
