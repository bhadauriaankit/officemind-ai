package com.officemind.application.system;

/**
 * Outbound port implemented by the infrastructure layer. Each adapter
 * (Postgres, Redis, MinIO, Kafka, Qdrant) reports its own reachability;
 * the application layer never talks to a driver/client directly.
 */
public interface InfrastructureHealthPort {

    ComponentStatus checkPostgres();

    ComponentStatus checkRedis();

    ComponentStatus checkObjectStorage();

    ComponentStatus checkMessageBroker();

    ComponentStatus checkVectorStore();

    record ComponentStatus(String component, boolean healthy, String detail) {
    }
}
