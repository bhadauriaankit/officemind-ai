package com.officemind.application.agent;

import com.officemind.domain.shared.EntityId;
import org.springframework.stereotype.Service;

@Service
public class DeleteAgentUseCase {

    private final AgentRepositoryPort agentRepository;

    public DeleteAgentUseCase(AgentRepositoryPort agentRepository) {
        this.agentRepository = agentRepository;
    }

    public void execute(EntityId id) {
        // No existence check needed: conversations.agent_id has ON DELETE
        // SET NULL (V9), so deleting a nonexistent agent is a harmless
        // no-op and deleting a real one safely detaches old conversations
        // rather than orphaning or blocking on them.
        agentRepository.deleteById(id);
    }
}
