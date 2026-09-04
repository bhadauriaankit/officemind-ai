package com.officemind.domain.aisettings;

import com.officemind.domain.shared.AggregateRoot;
import com.officemind.domain.shared.EntityId;

import java.time.Instant;
import java.util.Objects;

/**
 * Singleton configuration for the AI engine (model, temperature, system
 * prompt). There is exactly one row of this aggregate, seeded by migration
 * V6 with a fixed well-known id — see AiSettingsRepositoryPort.SETTINGS_ID.
 */
public class AiSettings extends AggregateRoot {

    private final EntityId id;
    private String modelName;
    private double temperature;
    private String systemPrompt;
    private Instant updatedAt;

    private AiSettings(EntityId id, String modelName, double temperature,
                        String systemPrompt, Instant updatedAt) {
        this.id = Objects.requireNonNull(id);
        this.modelName = Objects.requireNonNull(modelName, "modelName is required");
        this.temperature = temperature;
        this.systemPrompt = systemPrompt;
        this.updatedAt = updatedAt;
    }

    public static AiSettings rehydrate(EntityId id, String modelName, double temperature,
                                        String systemPrompt, Instant updatedAt) {
        return new AiSettings(id, modelName, temperature, systemPrompt, updatedAt);
    }

    public void update(String modelName, double temperature, String systemPrompt) {
        this.modelName = Objects.requireNonNull(modelName, "modelName is required");
        if (temperature < 0.0 || temperature > 2.0) {
            throw new IllegalArgumentException("temperature must be between 0.0 and 2.0");
        }
        this.temperature = temperature;
        this.systemPrompt = systemPrompt;
        this.updatedAt = Instant.now();
    }

    public EntityId getId() { return id; }
    public String getModelName() { return modelName; }
    public double getTemperature() { return temperature; }
    public String getSystemPrompt() { return systemPrompt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
