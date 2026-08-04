package com.officemind.application.system;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Aggregates the reachability of every Phase 1 infrastructure dependency
 * into a single platform health report. Backs the readiness endpoint used
 * by Kubernetes probes and by the Admin Portal's system dashboard (Phase 3).
 */
@Service
public class GetPlatformHealthUseCase {

    private final InfrastructureHealthPort healthPort;

    public GetPlatformHealthUseCase(InfrastructureHealthPort healthPort) {
        this.healthPort = healthPort;
    }

    public PlatformHealthReport execute() {
        List<InfrastructureHealthPort.ComponentStatus> statuses = List.of(
                healthPort.checkPostgres(),
                healthPort.checkRedis(),
                healthPort.checkObjectStorage(),
                healthPort.checkMessageBroker(),
                healthPort.checkVectorStore()
        );

        boolean allHealthy = statuses.stream().allMatch(InfrastructureHealthPort.ComponentStatus::healthy);
        return new PlatformHealthReport(allHealthy, statuses);
    }

    public record PlatformHealthReport(
            boolean healthy,
            List<InfrastructureHealthPort.ComponentStatus> components
    ) {
    }
}
