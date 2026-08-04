package com.officemind.application.system;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GetPlatformHealthUseCaseTest {

    private final InfrastructureHealthPort healthPort = mock(InfrastructureHealthPort.class);
    private final GetPlatformHealthUseCase useCase = new GetPlatformHealthUseCase(healthPort);

    @Test
    void reportsHealthyWhenAllComponentsAreUp() {
        stubAllComponents(true);

        GetPlatformHealthUseCase.PlatformHealthReport report = useCase.execute();

        assertThat(report.healthy()).isTrue();
        assertThat(report.components()).hasSize(5);
    }

    @Test
    void reportsUnhealthyWhenAnySingleComponentIsDown() {
        stubAllComponents(true);
        when(healthPort.checkVectorStore())
                .thenReturn(new InfrastructureHealthPort.ComponentStatus("qdrant", false, "connection refused"));

        GetPlatformHealthUseCase.PlatformHealthReport report = useCase.execute();

        assertThat(report.healthy()).isFalse();
        assertThat(report.components())
                .filteredOn(status -> status.component().equals("qdrant"))
                .first()
                .satisfies(status -> assertThat(status.healthy()).isFalse());
    }

    private void stubAllComponents(boolean healthy) {
        when(healthPort.checkPostgres())
                .thenReturn(new InfrastructureHealthPort.ComponentStatus("postgres", healthy, "ok"));
        when(healthPort.checkRedis())
                .thenReturn(new InfrastructureHealthPort.ComponentStatus("redis", healthy, "ok"));
        when(healthPort.checkObjectStorage())
                .thenReturn(new InfrastructureHealthPort.ComponentStatus("minio", healthy, "ok"));
        when(healthPort.checkMessageBroker())
                .thenReturn(new InfrastructureHealthPort.ComponentStatus("kafka", healthy, "ok"));
        when(healthPort.checkVectorStore())
                .thenReturn(new InfrastructureHealthPort.ComponentStatus("qdrant", healthy, "ok"));
    }
}
