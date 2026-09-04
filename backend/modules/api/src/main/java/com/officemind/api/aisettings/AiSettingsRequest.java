package com.officemind.api.aisettings;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AiSettingsRequest(
        @NotBlank(message = "modelName is required") String modelName,
        @NotNull @DecimalMin("0.0") @DecimalMax("2.0") Double temperature,
        String systemPrompt
) {
}
