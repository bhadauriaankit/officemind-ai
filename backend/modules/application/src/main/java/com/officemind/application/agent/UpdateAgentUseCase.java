package com.officemind.application.agent;

import com.officemind.common.exception.ResourceNotFoundException;
import com.officemind.domain.agent.Agent;
import com.officemind.domain.shared.EntityId;
import org.springframework.stereotype.Service;

@Service
public class UpdateAgentUseCase {

    private final AgentRepositoryPort agentRepository;

    public UpdateAgentUseCase(AgentRepositoryPort agentRepository) {
        this.agentRepository = agentRepository;
    }

    public Agent execute(EntityId id, String name, String description, String systemPrompt) {
        Agent agent = agentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agent", id));
        agent.update(name, description, systemPrompt);
        return agentRepository.save(agent);
    }
}
