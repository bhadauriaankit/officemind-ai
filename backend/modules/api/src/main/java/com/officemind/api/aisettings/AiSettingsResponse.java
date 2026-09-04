package com.officemind.api.aisettings;

import com.officemind.domain.aisettings.AiSettings;

import java.time.Instant;

public record AiSettingsResponse(
        String modelName,
        double temperature,
        String systemPrompt,
        Instant updatedAt
) {
    public static AiSettingsResponse from(AiSettings settings) {
        return new AiSettingsResponse(
                settings.getModelName(),
                settings.getTemperature(),
                settings.getSystemPrompt(),
                settings.getUpdatedAt()
        );
    }
}
