package com.officemind.infrastructure.agent;

import com.officemind.domain.agent.Agent;
import com.officemind.domain.shared.EntityId;

final class AgentMapper {

    private AgentMapper() {
    }

    static Agent toDomain(AgentJpaEntity entity) {
        return Agent.rehydrate(
                EntityId.of(entity.getId()),
                entity.getKey(),
                entity.getName(),
                entity.getDescription(),
                entity.getSystemPrompt(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    static AgentJpaEntity toJpa(Agent agent) {
        return new AgentJpaEntity(
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