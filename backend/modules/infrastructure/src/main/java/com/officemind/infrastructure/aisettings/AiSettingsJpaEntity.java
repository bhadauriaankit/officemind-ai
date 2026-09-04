package com.officemind.infrastructure.aisettings;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "ai_settings", schema = "officemind")
public class AiSettingsJpaEntity {

    @Id
    private UUID id;

    @Column(name = "model_name", nullable = false)
    private String modelName;

    @Column(nullable = false)
    private double temperature;

    @Column(name = "system_prompt")
    private String systemPrompt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected AiSettingsJpaEntity() {
    }

    public AiSettingsJpaEntity(UUID id, String modelName, double temperature,
                                String systemPrompt, Instant updatedAt) {
        this.id = id;
        this.modelName = modelName;
        this.temperature = temperature;
        this.systemPrompt = systemPrompt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() { return id; }
    public String getModelName() { return modelName; }
    public double getTemperature() { return temperature; }
    public String getSystemPrompt() { return systemPrompt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
