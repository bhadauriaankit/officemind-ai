package com.officemind.domain.shared;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Base class for DDD aggregate roots. Collects domain events raised during
 * a use case so the infrastructure layer can publish them to Kafka after the
 * transaction commits (transactional outbox pattern, wired in Phase 5+).
 */
public abstract class AggregateRoot {

    private final transient List<Object> domainEvents = new ArrayList<>();

    protected void registerEvent(Object event) {
        domainEvents.add(event);
    }

    public List<Object> pullDomainEvents() {
        List<Object> events = List.copyOf(domainEvents);
        domainEvents.clear();
        return Collections.unmodifiableList(events);
    }
}
