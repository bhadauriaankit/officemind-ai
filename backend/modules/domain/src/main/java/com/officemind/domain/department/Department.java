package com.officemind.domain.department;

import com.officemind.domain.shared.AggregateRoot;
import com.officemind.domain.shared.EntityId;

import java.time.Instant;
import java.util.Objects;

public class Department extends AggregateRoot {

    private final EntityId id;
    private String name;
    private String description;
    private final Instant createdAt;
    private Instant updatedAt;

    private Department(EntityId id, String name, String description, Instant createdAt, Instant updatedAt) {
        this.id = Objects.requireNonNull(id);
        this.name = Objects.requireNonNull(name, "name is required");
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Department create(String name, String description) {
        Instant now = Instant.now();
        return new Department(EntityId.generate(), name, description, now, now);
    }

    public static Department rehydrate(EntityId id, String name, String description,
                                        Instant createdAt, Instant updatedAt) {
        return new Department(id, name, description, createdAt, updatedAt);
    }

    public void rename(String name, String description) {
        this.name = Objects.requireNonNull(name, "name is required");
        this.description = description;
        this.updatedAt = Instant.now();
    }

    public EntityId getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
