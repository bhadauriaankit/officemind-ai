package com.officemind.infrastructure.aisettings;

import com.officemind.application.aisettings.AiSettingsRepositoryPort;
import com.officemind.domain.aisettings.AiSettings;
import org.springframework.stereotype.Component;

@Component
public class AiSettingsRepositoryAdapter implements AiSettingsRepositoryPort {

    private final AiSettingsJpaRepository jpaRepository;

    public AiSettingsRepositoryAdapter(AiSettingsJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public AiSettings get() {
        AiSettingsJpaEntity entity = jpaRepository.findById(SETTINGS_ID.value())
                .orElseThrow(() -> new IllegalStateException(
                        "ai_settings seed row is missing — expected id " + SETTINGS_ID.value()
                                + " to exist from migration V6"));
        return AiSettingsMapper.toDomain(entity);
    }

    @Override
    public AiSettings save(AiSettings settings) {
        AiSettingsJpaEntity saved = jpaRepository.save(AiSettingsMapper.toJpa(settings));
        return AiSettingsMapper.toDomain(saved);
    }
}