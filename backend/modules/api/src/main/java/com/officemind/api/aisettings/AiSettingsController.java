package com.officemind.api.aisettings;

import com.officemind.application.aisettings.GetAiSettingsUseCase;
import com.officemind.application.aisettings.UpdateAiSettingsUseCase;
import com.officemind.domain.aisettings.AiSettings;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ai-settings")
public class AiSettingsController {

    private final GetAiSettingsUseCase getAiSettingsUseCase;
    private final UpdateAiSettingsUseCase updateAiSettingsUseCase;

    public AiSettingsController(GetAiSettingsUseCase getAiSettingsUseCase,
                                 UpdateAiSettingsUseCase updateAiSettingsUseCase) {
        this.getAiSettingsUseCase = getAiSettingsUseCase;
        this.updateAiSettingsUseCase = updateAiSettingsUseCase;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public AiSettingsResponse get() {
        return AiSettingsResponse.from(getAiSettingsUseCase.execute());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping
    public AiSettingsResponse update(@Valid @RequestBody AiSettingsRequest request) {
        AiSettings updated = updateAiSettingsUseCase.execute(
                request.modelName(), request.temperature(), request.systemPrompt());
        return AiSettingsResponse.from(updated);
    }
}
