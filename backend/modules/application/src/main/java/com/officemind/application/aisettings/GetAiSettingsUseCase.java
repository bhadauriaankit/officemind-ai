package com.officemind.application.aisettings;

import com.officemind.domain.aisettings.AiSettings;
import org.springframework.stereotype.Service;

@Service
public class GetAiSettingsUseCase {

    private final AiSettingsRepositoryPort aiSettingsRepository;

    public GetAiSettingsUseCase(AiSettingsRepositoryPort aiSettingsRepository) {
        this.aiSettingsRepository = aiSettingsRepository;
    }

    public AiSettings execute() {
        return aiSettingsRepository.get();
    }
}
