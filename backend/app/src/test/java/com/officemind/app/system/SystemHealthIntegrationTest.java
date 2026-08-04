package com.officemind.app.system;
import com.officemind.app.OfficeMindApplication;
import io.minio.MinioClient;
import org.apache.kafka.clients.admin.AdminClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies the full vertical slice wired in Phase 1 (Module 1): a request
 * to /api/v1/system/health reaches the api -> application -> infrastructure
 * layers and successfully reports a real Postgres instance as healthy.
 *
 * MinIO/Kafka/Qdrant clients are mocked here rather than containerized to
 * keep this specific test fast and focused; a full multi-container smoke
 * test is added once those adapters gain real business logic in later
 * phases (RAG for Qdrant/MinIO, event publishing for Kafka).
 */
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = OfficeMindApplication.class)
class SystemHealthIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16.3-alpine")
            .withDatabaseName("officemind_test")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.data.redis.host", () -> "localhost");
        registry.add("officemind.storage.endpoint", () -> "http://localhost:9000");
        registry.add("officemind.storage.access-key", () -> "test");
        registry.add("officemind.storage.secret-key", () -> "test");
        registry.add("officemind.kafka.bootstrap-servers", () -> "localhost:9092");
        registry.add("officemind.qdrant.host", () -> "localhost");
        registry.add("officemind.qdrant.port", () -> "6334");
    }

    @MockBean
    private MinioClient minioClient;

    @MockBean
    private AdminClient kafkaAdminClient;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void healthEndpointReflectsRealPostgresConnectivity() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/api/v1/system/health", String.class);

        assertThat(response.getBody()).contains("\"component\":\"postgres\"");
        assertThat(response.getBody()).contains("\"healthy\":true");
    }
}
