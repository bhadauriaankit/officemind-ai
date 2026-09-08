package com.officemind.application.agent;

import com.officemind.common.exception.ConflictException;
import com.officemind.domain.agent.Agent;
import org.springframework.stereotype.Service;

@Service
public class CreateAgentUseCase {

    private final AgentRepositoryPort agentRepository;

    public CreateAgentUseCase(AgentRepositoryPort agentRepository) {
        this.agentRepository = agentRepository;
    }

    public Agent execute(String key, String name, String description, String systemPrompt) {
        agentRepository.findByKey(key).ifPresent(existing -> {
            throw new ConflictException("An agent with key '" + key + "' already exists");
        });
        return agentRepository.save(Agent.create(key, name, description, systemPrompt));
    }
}
