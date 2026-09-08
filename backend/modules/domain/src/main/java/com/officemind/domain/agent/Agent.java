package com.officemind.domain.agent;

import com.officemind.domain.shared.AggregateRoot;
import com.officemind.domain.shared.EntityId;

import java.time.Instant;
import java.util.Objects;

/**
 * A named persona (e.g. "HR Assistant") with its own system prompt,
 * selectable when starting a conversation. Phase 7 v1: prompt-only
 * specialization -- no tool-calling (see handover notes on why: Ollama's
 * spring-ai 1.0.0-M1 integration doesn't wire into the framework's
 * function-calling abstractions, verified via javap rather than assumed).
 */
public class Agent extends AggregateRoot {

    private final EntityId id;
    private String key;
    private String name;
    private String description;
    private String systemPrompt;
    private final Instant createdAt;
    private Instant updatedAt;

    private Agent(EntityId id, String key, String name, String description,
                  String systemPrompt, Instant createdAt, Instant updatedAt) {
        this.id = Objects.requireNonNull(id);
        this.key = Objects.requireNonNull(key, "key is required");
        this.name = Objects.requireNonNull(name, "name is required");
        this.description = description;
        this.systemPrompt = Objects.requireNonNull(systemPrompt, "systemPrompt is required");
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Agent create(String key, String name, String description, String systemPrompt) {
        Instant now = Instant.now();
        return new Agent(EntityId.generate(), key, name, description, systemPrompt, now, now);
    }

    public static Agent rehydrate(EntityId id, String key, String name, String description,
                                   String systemPrompt, Instant createdAt, Instant updatedAt) {
        return new Agent(id, key, name, description, systemPrompt, createdAt, updatedAt);
    }

    public void update(String name, String description, String systemPrompt) {
        this.name = Objects.requireNonNull(name, "name is required");
        this.description = description;
        this.systemPrompt = Objects.requireNonNull(systemPrompt, "systemPrompt is required");
        this.updatedAt = Instant.now();
    }

    public EntityId getId() { return id; }
    public String getKey() { return key; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getSystemPrompt() { return systemPrompt; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
