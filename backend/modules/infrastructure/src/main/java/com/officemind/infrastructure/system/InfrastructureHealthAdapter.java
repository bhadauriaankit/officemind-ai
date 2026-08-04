package com.officemind.infrastructure.system;

import com.officemind.application.system.InfrastructureHealthPort;
import io.minio.BucketExistsArgs;
import io.minio.MinioClient;
import io.qdrant.client.QdrantClient;
import org.apache.kafka.clients.admin.AdminClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Component
public class InfrastructureHealthAdapter implements InfrastructureHealthPort {

    private final DataSource dataSource;
    private final StringRedisTemplate redisTemplate;
    private final MinioClient minioClient;
    private final AdminClient kafkaAdminClient;
    private final QdrantClient qdrantClient;

    @Value("${officemind.storage.default-bucket:officemind-documents}")
    private String defaultBucket;

    public InfrastructureHealthAdapter(DataSource dataSource,
                                        StringRedisTemplate redisTemplate,
                                        MinioClient minioClient,
                                        AdminClient kafkaAdminClient,
                                        QdrantClient qdrantClient) {
        this.dataSource = dataSource;
        this.redisTemplate = redisTemplate;
        this.minioClient = minioClient;
        this.kafkaAdminClient = kafkaAdminClient;
        this.qdrantClient = qdrantClient;
    }

    @Override
    public ComponentStatus checkPostgres() {
        try (Connection connection = dataSource.getConnection()) {
            boolean valid = connection.isValid(2);
            return new ComponentStatus("postgres", valid, valid ? "connection validated" : "connection invalid");
        } catch (Exception e) {
            return new ComponentStatus("postgres", false, e.getMessage());
        }
    }

    @Override
    public ComponentStatus checkRedis() {
        try {
            String pong = redisTemplate.getConnectionFactory().getConnection().ping();
            boolean healthy = "PONG".equalsIgnoreCase(pong);
            return new ComponentStatus("redis", healthy, "PING -> " + pong);
        } catch (Exception e) {
            return new ComponentStatus("redis", false, e.getMessage());
        }
    }

    @Override
    public ComponentStatus checkObjectStorage() {
        try {
            boolean exists = minioClient.bucketExists(
                    BucketExistsArgs.builder().bucket(defaultBucket).build());
            return new ComponentStatus("minio", true,
                    exists ? "bucket '" + defaultBucket + "' present" : "reachable, bucket not yet created");
        } catch (Exception e) {
            return new ComponentStatus("minio", false, e.getMessage());
        }
    }

    @Override
    public ComponentStatus checkMessageBroker() {
        try {
            var result = kafkaAdminClient.describeCluster();
            String clusterId = result.clusterId().get(3, TimeUnit.SECONDS);
            return new ComponentStatus("kafka", clusterId != null, "cluster id: " + clusterId);
        } catch (Exception e) {
            return new ComponentStatus("kafka", false, e.getMessage());
        }
    }

    @Override
    public ComponentStatus checkVectorStore() {
        try {
            var collections = qdrantClient.listCollectionsAsync(Duration.ofSeconds(3))
                    .get(3, TimeUnit.SECONDS);
            return new ComponentStatus("qdrant", true, "collections: " + collections.size());
        } catch (Exception e) {
            return new ComponentStatus("qdrant", false, e.getMessage());
        }
    }
}
