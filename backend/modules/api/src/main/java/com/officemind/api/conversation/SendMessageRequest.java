package com.officemind.api.conversation;

import jakarta.validation.constraints.NotBlank;

public record SendMessageRequest(
        @NotBlank(message = "message is required") String message
) {
}
