package com.officemind.api.agent;

import jakarta.validation.constraints.NotBlank;

public record AgentRequest(
        // key is only used on create; ignored on update (an agent's key
        // is its stable identity once created).
        String key,
        @NotBlank(message = "name is required") String name,
        String description,
        @NotBlank(message = "systemPrompt is required") String systemPrompt
) {
}
