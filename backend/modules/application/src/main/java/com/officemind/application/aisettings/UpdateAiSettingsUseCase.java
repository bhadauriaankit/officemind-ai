package com.officemind.application.aisettings;

import com.officemind.domain.aisettings.AiSettings;
import org.springframework.stereotype.Service;

@Service
public class UpdateAiSettingsUseCase {

    private final AiSettingsRepositoryPort aiSettingsRepository;

    public UpdateAiSettingsUseCase(AiSettingsRepositoryPort aiSettingsRepository) {
        this.aiSettingsRepository = aiSettingsRepository;
    }

    public AiSettings execute(String modelName, double temperature, String systemPrompt) {
        AiSettings settings = aiSettingsRepository.get();
        settings.update(modelName, temperature, systemPrompt);
        return aiSettingsRepository.save(settings);
    }
}
