package com.officemind.application.agent;

import com.officemind.domain.agent.Agent;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListAgentsUseCase {

    private final AgentRepositoryPort agentRepository;

    public ListAgentsUseCase(AgentRepositoryPort agentRepository) {
        this.agentRepository = agentRepository;
    }

    public List<Agent> execute() {
        return agentRepository.findAll();
    }
}
