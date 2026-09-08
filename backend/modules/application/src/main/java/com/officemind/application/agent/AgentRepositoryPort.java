package com.officemind.application.agent;

import com.officemind.domain.agent.Agent;
import com.officemind.domain.shared.EntityId;

import java.util.List;
import java.util.Optional;

public interface AgentRepositoryPort {

    Agent save(Agent agent);

    Optional<Agent> findById(EntityId id);

    Optional<Agent> findByKey(String key);

    // Agents are admin-managed and expected to stay small in number (a
    // handful of personas), so a flat list is simpler than paging here --
    // unlike Users/Documents/Conversations which can grow unbounded.
    List<Agent> findAll();

    void deleteById(EntityId id);
}
