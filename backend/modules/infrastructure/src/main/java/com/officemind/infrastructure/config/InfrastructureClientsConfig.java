package com.officemind.infrastructure.config;

import io.minio.MinioClient;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.QdrantGrpcClient;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * Wires low-level clients for the external systems introduced in Phase 1.
 * Kept in `infrastructure` (not `app`) so these beans stay swappable behind
 * the ports defined in the `application` module.
 */
@Configuration
public class InfrastructureClientsConfig {

    @Bean
    public MinioClient minioClient(
            @Value("${officemind.storage.endpoint}") String endpoint,
            @Value("${officemind.storage.access-key}") String accessKey,
            @Value("${officemind.storage.secret-key}") String secretKey) {
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }

    @Bean(destroyMethod = "close")
    public AdminClient kafkaAdminClient(
            @Value("${officemind.kafka.bootstrap-servers}") String bootstrapServers) {
        return AdminClient.create(Map.of(
                AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, "5000"
        ));
    }

    @Bean
    public QdrantClient qdrantClient(
            @Value("${officemind.qdrant.host}") String host,
            @Value("${officemind.qdrant.port}") int port) {
        return new QdrantClient(QdrantGrpcClient.newBuilder(host, port, false).build());
    }
}
