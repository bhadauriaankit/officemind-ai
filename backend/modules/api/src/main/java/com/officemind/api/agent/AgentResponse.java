package com.officemind.api.agent;

import com.officemind.domain.agent.Agent;

import java.time.Instant;
import java.util.UUID;

public record AgentResponse(
        UUID id,
        String key,
        String name,
        String description,
        String systemPrompt,
        Instant createdAt,
        Instant updatedAt
) {
    public static AgentResponse from(Agent agent) {
        return new AgentResponse(
                agent.getId().value(),
                agent.getKey(),
                agent.getName(),
                agent.getDescription(),
                agent.getSystemPrompt(),
                agent.getCreatedAt(),
                agent.getUpdatedAt()
        );
    }
}
