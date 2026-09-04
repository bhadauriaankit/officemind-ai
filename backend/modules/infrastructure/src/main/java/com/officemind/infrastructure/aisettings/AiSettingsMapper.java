package com.officemind.infrastructure.aisettings;

import com.officemind.domain.aisettings.AiSettings;
import com.officemind.domain.shared.EntityId;

final class AiSettingsMapper {

    private AiSettingsMapper() {
    }

    static AiSettings toDomain(AiSettingsJpaEntity entity) {
        return AiSettings.rehydrate(
                EntityId.of(entity.getId()),
                entity.getModelName(),
                entity.getTemperature(),
                entity.getSystemPrompt(),
                entity.getUpdatedAt()
        );
    }

    static AiSettingsJpaEntity toJpa(AiSettings settings) {
        return new AiSettingsJpaEntity(
                settings.getId().value(),
                settings.getModelName(),
                settings.getTemperature(),
                settings.getSystemPrompt(),
                settings.getUpdatedAt()
        );
    }
}
