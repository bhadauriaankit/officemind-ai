package com.officemind.api.conversation;

import jakarta.validation.constraints.NotBlank;

public record SendMessageRequest(
        @NotBlank(message = "message is required") String message,
        // Only meaningful on the "start new conversation" endpoint; ignored
        // on "continue conversation" since the agent is fixed at start.
        String agentId
) {
}